package com.atguigu.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atguigu.utils.VerifyCodeConfig;

import redis.clients.jedis.Jedis;

/**
 * Servlet implementation class CodeVerifyServlet
 */
public class CodeVerifyServlet extends HttpServlet {
	
 
    
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CodeVerifyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

 
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//1.获取两个参数,并验空
		String phoneNo = request.getParameter("phone_no");
		String verifyCode = request.getParameter("verify_code");
		if(phoneNo==null||verifyCode==null) {
			return ;
		}
		//2.获取验证码
		Jedis jedis = new Jedis("192.168.193.128",6379);
		String codeKey = VerifyCodeConfig.PHONE_PREFIX+phoneNo+VerifyCodeConfig.PHONE_SUFFIX;
		String code = jedis.get(codeKey);
		jedis.close();
		
		//3.判断
		if(verifyCode.equals(code)) {
			//4.返回结果
			response.getWriter().print(true);
		}
	}

}
