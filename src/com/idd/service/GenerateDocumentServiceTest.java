package com.idd.service;

import java.sql.SQLException;

public class GenerateDocumentServiceTest {
	
	public static void main(String[] args) throws SQLException {

		try {
			Object result = gendoc();
			System.out.println(result);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String gendoc() throws SQLException {
		
		return new GenerateDocumentService()
					.generate(
						"dataSourceTest",
						"GenerateDocument",
						"POSv1.0",
						"DVT_CapNhatPhi.docx",
						"EXIM_TB_CAPNHATPHI.docx",
						"[{\"SoDVCapNhatPhi\":\"\",\"NgayLapThongBao\":\"....................\",\"ChiNhanh\":\"\",\"PGD\":\"\",\"SoHDHT\":\"\",\"Ngay_RMTL_P_Duyet\":\"....................\",\"LoaiYC\":\"\",\"TID\":\"TID000123\",\"MID\":\"MID000456\",\"MCC\":\"600\",\"MasterMerchant\":\"Cong ty TNHH Thuong mai ABC\",\"TenThietBi\":\"May POS EDC\",\"DiaChiLapThietBi\":\"123 Nguyen Trai Phuong Ben Thanh Quan 1 TP Ho Chi Minh\",\"LoaiBaoCo\":\"BC_THEOPHIEN\",\"ThongTinTKBaoCo\":\"Cong ty TNHH Thuong mai ABC - 102345678901; Nguyen Van A - 0123456789; Tai khoan trung gian thanh toan - 998877665544\",\"MakerDVKD_Name\":\"\",\"CheckerDVKD_Name\":\"\",\"Ngay_RMTL_P_Ky\":\"....................\",\"MakerHO_Name\":\"\",\"CheckerHO_Name\":\"\",\"Ngay_CheckerHO_Ky\":\"....................\",\"Tbl_ThongTinPhi\":\"[{\\\"Tbl_ThongTinPhi_Title_Upper\\\":1,\\\"Tbl_ThongTinPhi_Title\\\":\\\"I. Domestic (Thẻ do Ngân hàng VN phát hành)\\\"},{\\\"Tbl_ThongTinPhi_Title\\\":\\\"JCB Card\\\",\\\"Tbl_ThongTinPhi_MDR1\\\":null,\\\"Tbl_ThongTinPhi_MDR2\\\":null},{\\\"Tbl_ThongTinPhi_Title\\\":\\\"Mastercard\\\",\\\"Tbl_ThongTinPhi_MDR1\\\":null,\\\"Tbl_ThongTinPhi_MDR2\\\":null},{\\\"Tbl_ThongTinPhi_Title\\\":\\\"NAPAS CR\\\",\\\"Tbl_ThongTinPhi_MDR1\\\":null,\\\"Tbl_ThongTinPhi_MDR2\\\":null},{\\\"Tbl_ThongTinPhi_Title\\\":\\\"NAPAS DB\\\",\\\"Tbl_ThongTinPhi_MDR1\\\":null,\\\"Tbl_ThongTinPhi_MDR2\\\":null},{\\\"Tbl_ThongTinPhi_Title\\\":\\\"UPI\\\",\\\"Tbl_ThongTinPhi_MDR1\\\":null,\\\"Tbl_ThongTinPhi_MDR2\\\":null},{\\\"Tbl_ThongTinPhi_Title\\\":\\\"VISA\\\",\\\"Tbl_ThongTinPhi_MDR1\\\":null,\\\"Tbl_ThongTinPhi_MDR2\\\":null},{\\\"Tbl_ThongTinPhi_Title_Upper\\\":1,\\\"Tbl_ThongTinPhi_Title\\\":\\\"II. International (Thẻ do Ngân hàng Nước ngoài phát hành)\\\"},{\\\"Tbl_ThongTinPhi_Title\\\":\\\"JCB Card\\\",\\\"Tbl_ThongTinPhi_MDR1\\\":null,\\\"Tbl_ThongTinPhi_MDR2\\\":null},{\\\"Tbl_ThongTinPhi_Title\\\":\\\"Mastercard\\\",\\\"Tbl_ThongTinPhi_MDR1\\\":null,\\\"Tbl_ThongTinPhi_MDR2\\\":null},{\\\"Tbl_ThongTinPhi_Title\\\":\\\"UPI\\\",\\\"Tbl_ThongTinPhi_MDR1\\\":null,\\\"Tbl_ThongTinPhi_MDR2\\\":null},{\\\"Tbl_ThongTinPhi_Title\\\":\\\"VISA\\\",\\\"Tbl_ThongTinPhi_MDR1\\\":null,\\\"Tbl_ThongTinPhi_MDR2\\\":null},{\\\"Tbl_ThongTinPhi_Title_Upper\\\":1,\\\"Tbl_ThongTinPhi_Title\\\":\\\"III. Our (Thẻ do Ngân hàng Eximbank phát hành)\\\"},{\\\"Tbl_ThongTinPhi_Title\\\":\\\"JCB Card\\\",\\\"Tbl_ThongTinPhi_MDR1\\\":null,\\\"Tbl_ThongTinPhi_MDR2\\\":null},{\\\"Tbl_ThongTinPhi_Title\\\":\\\"Mastercard\\\",\\\"Tbl_ThongTinPhi_MDR1\\\":null,\\\"Tbl_ThongTinPhi_MDR2\\\":null},{\\\"Tbl_ThongTinPhi_Title\\\":\\\"UPI\\\",\\\"Tbl_ThongTinPhi_MDR1\\\":null,\\\"Tbl_ThongTinPhi_MDR2\\\":null},{\\\"Tbl_ThongTinPhi_Title\\\":\\\"VISA\\\",\\\"Tbl_ThongTinPhi_MDR1\\\":null,\\\"Tbl_ThongTinPhi_MDR2\\\":null},{\\\"Tbl_ThongTinPhi_Title\\\":\\\"VTOP NAPAS CR\\\",\\\"Tbl_ThongTinPhi_MDR1\\\":null,\\\"Tbl_ThongTinPhi_MDR2\\\":null},{\\\"Tbl_ThongTinPhi_Title\\\":\\\"VTOP NAPAS DB\\\",\\\"Tbl_ThongTinPhi_MDR1\\\":null,\\\"Tbl_ThongTinPhi_MDR2\\\":null}]\",\"Tbl_ThongTinPhiCash\":\"[]\"}]",
						"tYtPIDwQBTuevzK8NhOXQw==",
						"hehehehe"
					);
	}
}
