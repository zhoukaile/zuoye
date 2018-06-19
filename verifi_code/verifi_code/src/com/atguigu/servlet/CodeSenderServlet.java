package com.atguigu.servlet;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atguigu.utils.SmsUtil;
import com.atguigu.utils.VerifyCodeConfig;

import redis.clients.jedis.Jedis;

/**
 * Servlet implementation class VerifiCodeServlet
 */
public class CodeSenderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CodeSenderServlet() {
        super();
        // TODO Auto-generated constructor stub
    }


    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
     
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//1.获取参数,验空
		//请求域中获取到电话号
		String phoneNo = request.getParameter("phone_no");
		//判断电话号是否为空
		if(phoneNo==null) {
			return ;
		}
		
		//1.5  验证每个手机号每天最多发送三次
		//取计数器
		Jedis jedis = new Jedis("192.168.193.128",6379);
		String countKey = VerifyCodeConfig.PHONE_PREFIX+phoneNo+VerifyCodeConfig.COUNT_SUFFIX;
		String countStr = jedis.get(countKey);
		
		//计数器是否为空,创建计数器+1
		if(countStr==null) {
			jedis.setex(countKey, VerifyCodeConfig.SECONDS_PER_DAY, "1");
		}else {
			//如果不为空,判断计数器次数,如果=3返回
			int count  = Integer.parseInt(countStr);
			if(count>=VerifyCodeConfig.COUNT_TIMES_1DAY) {
				response.getWriter().print("limit");
				jedis.close();
				return ;
			}else {
				//计数器小于3,则+1
				jedis.incr(countKey);
			}
		}
		
		//2.获取验证码
		String code = genCode(VerifyCodeConfig.CODE_LEN);
		
		//3.保存验证码
		//Jedis jedis = new Jedis("192.168.193.128",6379);
		//获取key值
		String codeKey = VerifyCodeConfig.PHONE_PREFIX+phoneNo+VerifyCodeConfig.PHONE_SUFFIX;
		//key:codeKey   时间:VerifyCodeConfig.CODE_TIMEOUT   value:code
		jedis.setex(codeKey, VerifyCodeConfig.CODE_TIMEOUT, code);
		
		//记得要关闭jedis
		jedis.close();
		//4.发送验证码
		System.out.println(code);
		
		//5.返回
		response.getWriter().print(true);
		  
		  
		 
		
	} 
	
	
	private   String genCode(int len){
		 String code="";
		 for (int i = 0; i < len; i++) {
		     int rand=  new Random().nextInt(10);
		     code+=rand;
		 }
		 
		return code;
	}
	
	
 
}
