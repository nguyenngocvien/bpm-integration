package com.idd.module.mail;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.idd.config.cache.ServiceConfigCache;
import com.idd.config.entity.ERROR_CODE;
import com.idd.config.entity.LogRecord;
import com.idd.config.entity.Response;
import com.idd.config.entity.ServiceConfig;
import com.idd.module.sql.SQLConnector;
import com.idd.shared.crypto.CryptoService;
import com.idd.shared.util.JsonHelper;
import com.idd.shared.util.LogHelper;

public class EmailSender extends SQLConnector {

	private final static String SYSTEM = "MAIL";
	private final LogHelper logHelper;
	private final ServiceConfigCache serviceConfigCache;
	private final CryptoService cryptoService;
	private final EmailTemplateRepository emailTemplateRepository;
	private final SmtpEmailSender emailSender;

	public EmailSender(String dataSourceName, String secretKey) {
		super(dataSourceName);
		this.logHelper = new LogHelper(dataSourceName);
		this.serviceConfigCache = new ServiceConfigCache(dataSourceName);
		this.cryptoService = new CryptoService(Base64.getDecoder().decode(secretKey));
		this.emailTemplateRepository = new EmailTemplateRepository(dataSourceName);
		this.emailSender = new SmtpEmailSender();
	}

	public Response send(String processCode, String templateCode, String version, String toEmails, String ccEmails,
			String bccEmails, String inputData, String caseId) {

		String serviceName = processCode + "." + templateCode;

		Map<String, Object> fromInput = new HashMap<>();
		fromInput.put("serviceName", serviceName);
		fromInput.put("version", version);
		fromInput.put("caseId", caseId);
		fromInput.put("toEmails", toEmails);
		fromInput.put("ccEmails", ccEmails);
		fromInput.put("bccEmails", bccEmails);
		fromInput.put("inputData", inputData);

		LogRecord log = LogRecord.init(
				caseId,
				serviceName,
				JsonHelper.stringify(fromInput),
				SYSTEM);

		Response response = null;

		try {
			if (toEmails == null || toEmails.isEmpty()) {
				throw new IllegalArgumentException(
						"Email recipient list is empty");
			}

			ServiceConfig serviceConfig = serviceConfigCache.get(serviceName, version);
			if (serviceConfig == null) {
				throw new IllegalStateException(
						"Service config not found for " + serviceName);
			}

			EmailTemplate template = emailTemplateRepository.findActiveTemplate(processCode, templateCode);
			if (template == null) {
				throw new IllegalStateException(
						"Template not found for " + processCode + " / EMAIL / " + templateCode);
			}

			List<String> to = Arrays.asList(toEmails.split(","));

			List<String> cc = Arrays.asList(ccEmails.split(","));

			List<String> bcc = Arrays.asList(bccEmails.split(","));

			// ================= TO / CC / BCC =================
			List<String> toList = EmailUtil.filterValidEmails(to);
			List<String> ccList = EmailUtil.filterValidEmails(cc);
			List<String> bccList = EmailUtil.filterValidEmails(bcc);

			if (toList.isEmpty()) {
				throw new IllegalArgumentException(
						"No valid email address after validation");
			}

			Map<String, Object> inputMap = JsonHelper.parseToMap(inputData);

			String subject = EmailUtil.renderContent(template.getTitle(), inputMap);
			String content = EmailUtil.renderContent(template.getContent(), inputMap);

			EmailMessage email = new EmailMessage();
			email.setTo(String.join(",", toList));
			email.setCc(String.join(",", ccList));
			email.setBcc(String.join(",", bccList));
			email.setSubject(subject);
			email.setContent(content);
			email.setHtml(true);
			log.setToInput(JsonHelper.stringify(email));

			SmtpConfig smtpConfig = JsonHelper.parseObject(serviceConfig.getDetailConfig(), SmtpConfig.class);
			if (smtpConfig == null) {
				throw new IllegalStateException(
						String.format(
								"Detail of service config is invalid for serviceName=%s, version=%s",
								serviceName,
								version));
			}
			smtpConfig.setPassword(cryptoService.decrypt(smtpConfig.getPassword()));

			emailSender.send(smtpConfig, email);

			Long logId = logHelper.saveSuccess(
					log,
					serviceConfig.isLogEnabled(),
					null);

			response = Response.success(logId);

		} catch (Exception e) {
			Long logId = logHelper.saveError(
					log,
					e,
					ERROR_CODE.ERROR);

			String message = e.getMessage() != null
					? e.getMessage()
					: e.getClass().getSimpleName();

			response = Response.error(ERROR_CODE.ERROR.getCode(), message, logId);
		}

		return response;
	}
}