/// HTTPS版短信发送DEMO,通过web service接口发送短信
/// 开发环境 ：JSE1.8,httpclient4.5.2,windows 10 专业版
/// 联系方式 ：346910917@qq.com,18611729367
/// 版本：1.1
/// 最近修订：2016-12-28

package cn.tellsea.utils;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.MimeHeaders;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//HTTPS版本短信发送DEMO
public class SendViaWs {

	private static HttpClient httpclient;
	private static final Logger logger = LogManager.getLogger(SendViaWs.class);
	public static void sendmsg(String mobs,String cnts) throws Exception {
		httpclient = new SSLClient();
		String wsdl = "https://dx.ipyy.net/webservice.asmx?wsdl";
		String userName="8T00128";										//账号名
		String password="8T0012888";										//发送密码
		String mobiles=mobs;//"17717285856";							//多个手机号用“,”分隔
		String content = cnts;//"java版调用soap接口，您的验证码：8888【华信】";
		String extNumber="";
		
        //定时短信需指定此字段，须UTC格式：2016-12-06T08:09:10+08:00
		String planSendTime="";
		
		HttpPost post = new HttpPost(wsdl);
		post.setHeader("Content-Type","application/soap+xml; charset=UTF-8");
		String soapData=buildSoapData(userName, password,mobiles, content,extNumber,planSendTime);
		StringEntity se=new StringEntity(soapData,"UTF-8");
		post.setEntity(se);
		HttpResponse response = httpclient.execute(post);	
		try {
			System.out.println(response.getStatusLine());
			HttpEntity entity = response.getEntity();
			// 将字符转化为XML
			String returnString = EntityUtils.toString(entity, "UTF-8");
			
			SOAPMessage msg=formatSoapString(returnString);
			msg.writeTo(System.out);
			SOAPBody body=msg.getSOAPBody();
			Map<String,String> map=new HashMap<String,String>();
			Iterator<SOAPElement> iterator=body.getChildElements();
			parseSoap(iterator,map);
			
			/*
			for(String item :map.keySet()){
				System.out.println(String.format("%1s:%2s", item,map.get(item)));
			}
			*/
			
	       // logger.warn("返回信息提示："+map.get("Description"));
	       // logger.warn("返回状态为："+map.get("StatusCode"));
	       // logger.warn("返回余额："+map.get("Amount"));
	       // logger.warn("返回本次任务ID："+map.get("MsgId"));
	        logger.warn("返回成功短信数："+map.get("SuccessCounts"));
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
		
	}
    
    private static String buildSoapData(String userName,String password,String mobiles,String content,String extNumber,String planSendTime) throws UnsupportedEncodingException{
    	StringBuffer soap=new StringBuffer();
    	
    	soap.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
    	soap.append("<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">");
    	soap.append("<soap12:Body>");
        soap.append("<SendSms xmlns=\"http://www.yysms.com/\">");
        soap.append("<userName>"+userName+"</userName>");
        soap.append("<password>"+password+"</password>");
        soap.append("<sms>");
        soap.append("<Msisdns>"+mobiles+"</Msisdns>");
        soap.append("<SMSContent><![CDATA["+content+"]]></SMSContent>");
        soap.append("<ExtNumber>"+extNumber+"</ExtNumber>");
        soap.append("<PlanSendTime xsi:nil='true'>"+planSendTime+"</PlanSendTime>");
        soap.append("</sms>");
        soap.append("</SendSms>");
        soap.append("</soap12:Body>");
        soap.append("</soap12:Envelope>");
    	return soap.toString();
    }	
    
	/**
	 * 把soap字符串格式化为SOAPMessage
	 * 
	 * @param soapString
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	private static SOAPMessage formatSoapString(String soapString) {
		MessageFactory msgFactory;
		try {
			msgFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
			SOAPMessage reqMsg = msgFactory.createMessage(new MimeHeaders(),
					new ByteArrayInputStream(soapString.getBytes("UTF-8")));
			reqMsg.saveChanges();
			return reqMsg;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static void parseSoap(Iterator<SOAPElement> iterator, Map<String, String> map){
		while(iterator.hasNext()){
			SOAPElement element=iterator.next();
			if("SendSmsResult".equals(element.getParentElement().getNodeName())){
				map.put(element.getNodeName(),element.getValue());
			}
			if(null==element.getValue() && element.getChildElements().hasNext()){
				parseSoap(element.getChildElements(),map);
			}
		}
	}
}

