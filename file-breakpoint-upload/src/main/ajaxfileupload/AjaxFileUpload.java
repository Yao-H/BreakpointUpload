package main.ajaxfileupload;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description(Ajax�ļ��ϴ�������)
 * @date 2017��9��3�� ����5:41:18
 * @author HuangYao
 * @version v1.0
 * @since v1.0
 *
 */
public class AjaxFileUpload extends HttpServlet{

	private static final long serialVersionUID = 152832604880736682L;

	public void doGet(HttpServletRequest req, HttpServletResponse res) {
		this.doPost(req, res);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) {
		try {
			req.getRequestDispatcher("/page/fileUpload.jsp").forward(req, res);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
