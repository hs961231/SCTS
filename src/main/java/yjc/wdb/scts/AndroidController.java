package yjc.wdb.scts;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import yjc.wdb.scts.bean.UserVO;
import yjc.wdb.scts.bean.BillVO;
import yjc.wdb.scts.bean.CouponVO;
import yjc.wdb.scts.bean.Coupon_holdVO;
import yjc.wdb.scts.bean.GoodsVO;
import yjc.wdb.scts.service.AndroidService;
import yjc.wdb.scts.service.CourseService;
import yjc.wdb.scts.service.UserService;

@Controller
@RequestMapping("/android")
public class AndroidController {
	
	@Inject
	private UserService userService;

	@Inject
	private CourseService courseService;


	@Inject
	private AndroidService androidService;


	private static final Logger logger = LoggerFactory.getLogger(AndroidController.class);


	// �޼�¡
	public final static String AUTH_KEY_FCM = "AAAAC8CC5Gc:APA91bGjNXJsXudD_Cmimb9WyhrnSsARuNb9rwI_dPLbxqMFn5mYg0V027JWRzmTjQpn_9BiWWKPlv-5LyH5KysYukKPl3Gmz60NAC7fN3tnEAMr_HQzd3jFSBuSOVf2D8qqa-cO13rm";
	public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

	// �ȵ���̵忡�� ���۵Ǵ� �α��� ������ ����
	@RequestMapping(value="/androidLogin", produces = "text/plain; charset=UTF-8")
	@ResponseBody
	public String androidLogin(HttpServletRequest request) throws Exception{
		// ���� �����ϱ� ���� �߰���
		// request ��ü �ȿ� �Ѿ���� �Ķ���Ͱ� ���ϴ� ���� ������ ��� ��������� ���� ��� error ��� ���ڿ��� ������
		String str = request.getParameter("UserVO");
		if(str == null) {
			return "ERROR";
		}
		
	

		JSONObject userJson = (JSONObject) new JSONParser().parse( str );

		UserVO user = new UserVO();
		try {

			user.setUser_id( userJson.get("user_id").toString() );
			user.setUser_password(userJson.get("user_pw").toString());
			user.setToken(userJson.get("token").toString());
			
			System.out.println(user.getUser_id() + " " + user.getToken());
			
			androidService.updateToken(user);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		if(androidService.androidLoginUser(user) == 1)
			return "SUCCESS";
		else
			return "ERROR";

	}


	// �ȵ���̵忡�� �������� ����
	/*
	 * ó�� ������ ���������� �ش��ϴ� ������ ������ �ٷ� ���������� �����ؼ� 
	 * ������ ������ �����Ͽ� �ǽð��� ����� �� �ֵ��� �ϴ� ����
	 */
	@RequestMapping(value="/firstCourse", produces = "text/plain; charset=UTF-8")
	@ResponseBody
	public String firstCourse(HttpServletRequest request) throws Exception{
		// ���� �����ϱ� ���� �߰���
		// request ��ü �ȿ� �Ѿ���� �Ķ���Ͱ� ���ϴ� ���� ������ ��� ��������� ���� ��� error ��� ���ڿ��� ������
		String str = request.getParameter("CourseVO");

		JSONObject resultData = new JSONObject();

		if(str == null) {
			resultData.put("status", "ERROR");
			resultData.put("errorCode", "CourseEmpty");
			return resultData.toString();
		}

		// �Ѿ�� ���ڿ��� json ��ü�� ��ȯ
		JSONObject courseJson = (JSONObject) new JSONParser().parse( str );

		//PositionVO position = new PositionVO();
		HashMap<String, String> vo = new HashMap<String, String>();

		try {
			// ���ڿ� ������ ��¥�ð� ���� timestamp������ ��ȯ
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
			java.util.Date parsedDate = dateFormat.parse( (String) courseJson.get("cours_pasng_time") );
			Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());

			vo.put("beacon_mjr", courseJson.get("beacon_mjr").toString() );
			vo.put("beacon_mnr", courseJson.get("beacon_mnr").toString() );
			vo.put("user_id", courseJson.get("user_id").toString() );
			vo.put("cours_pasng_time", timestamp.toString() );
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.debug("����");
			resultData.put("status", "ERROR");
			return resultData.toString();
		}

		// ��� ����
		try {
			courseService.insertCourse(vo);
		}
		catch (Exception e) {
			e.printStackTrace();
			resultData.put("status", "ERROR");
			resultData.put("errorCode", "BeaconNotTileSetting");
			return resultData.toString();
		}

		// �ȵ���̵�� ���� ������ ������ ���ؼ� ���
		CouponVO coupon = androidService.selectSendAndroidCoupon(vo);

		if(coupon == null) {
			resultData.put("status", "SUCCESS");
			resultData.put("command", "emptycoupon");
			return resultData.toString();
		}

		str = new Gson().toJson(coupon);
		resultData = (JSONObject) new JSONParser().parse(str);

		resultData.put("status", "SUCCESS");
		resultData.put("command", "fullcoupon");

		logger.debug(resultData.toString());

		return resultData.toString();
	}

	/*
	 * �ι�° ���� ����, �� ����� ���� �ɶ���
	 * �ȵ���̵� �������� ���� ���� ����� ������ ������ �Ͼ�� ������ �����ϰ� �ִ� ������ �ӹ� �ð��� ���� ��ų �� �� ����� ���� ��
	 */
	@RequestMapping(value="/secondCourse", produces = "text/plain; charset=UTF-8")
	@ResponseBody
	public String secondCourse(HttpServletRequest request) throws Exception{
		// ���� �����ϱ� ���� �߰���
		// request ��ü �ȿ� �Ѿ���� �Ķ���Ͱ� ���ϴ� ���� ������ ��� ��������� ���� ��� error ��� ���ڿ��� ������
		String str = request.getParameter("CourseVO");

		JSONObject resultData = new JSONObject();

		if(str == null) {
			resultData.put("status", "ERROR");
			return resultData.toString();
		}

		// �Ѿ�� ���ڿ��� json ��ü�� ��ȯ
		JSONObject courseJson = (JSONObject) new JSONParser().parse( str );

		//PositionVO position = new PositionVO();
		HashMap<String, String> vo = new HashMap<String, String>();

		try {
			// ���ڿ� ������ ��¥�ð� ���� timestamp������ ��ȯ
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
			java.util.Date parsedDate = dateFormat.parse( (String) courseJson.get("cours_pasng_time") );
			Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());

			vo.put("beacon_mjr", courseJson.get("beacon_mjr").toString() );
			vo.put("beacon_mnr", courseJson.get("beacon_mnr").toString() );
			vo.put("user_id", courseJson.get("user_id").toString() );
			vo.put("cours_stay_time", courseJson.get("cours_stay_time").toString() );
			vo.put("cours_pasng_time", timestamp.toString() );
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.debug("����");
			resultData.put("status", "ERROR");
			return resultData.toString();
		}

		logger.debug(str);
		logger.debug(vo.toString());

		// ��� �ӹ��ð� ����
		//positionService.insertPosition(position);
		courseService.updateStayTime(vo);


		resultData.put("status", "SUCCESS");
		resultData.put("command", "emptycoupon");

		logger.debug(resultData.toString());

		return resultData.toString();
	}

	// ȸ������
	@RequestMapping(value="checkUser", method=RequestMethod.POST, produces = "text/plain; charset=UTF-8")
	public @ResponseBody String checkUser(String user_id, HttpServletRequest request) throws Exception{
		System.out.print(request.getParameter("user_id"));
		int checkUser = androidService.checkUser(user_id);
		return ""+checkUser;
	}
	@RequestMapping(value="signUp", method=RequestMethod.POST)
	public @ResponseBody String signUp(HttpServletRequest request) throws Exception{

		System.out.println(request.getParameter("json"));
		String str = request.getParameter("json");
		JSONObject joinJSON = (JSONObject) new JSONParser().parse(str);

		UserVO user = new UserVO();
		user.setUser_id(joinJSON.get("user_id").toString());
		user.setUser_password((String) joinJSON.get("user_pw").toString());
		//user.setAge(Integer.parseInt(joinJSON.get("age").toString()));
		user.setUser_sexdstn(joinJSON.get("gender").toString());



		userService.insertUser(user);



		int checkUser = androidService.checkUser(user.getUser_id());
		return ""+checkUser;
	}

	// �̺�Ʈ ����
	@RequestMapping(value="eventList", method=RequestMethod.GET)
	public @ResponseBody String eventList(HttpServletRequest request) throws Exception{

		String callback = request.getParameter("callback");
		List<HashMap> list = androidService.eventList();
		JSONObject eventJson;
		JSONObject event;
		JSONArray eventArray = new JSONArray();
		for(int i = 0; i < list.size(); i++){
			eventJson = new JSONObject();
			eventJson.put("bbsctt_code", list.get(i).get("bbsctt_code"));
			eventJson.put("event_begin_de", list.get(i).get("event_begin_de").toString());
			eventJson.put("event_end_de", list.get(i).get("event_end_de").toString());
			eventJson.put("bbsctt_sj", list.get(i).get("bbsctt_sj"));

			eventArray.add(eventJson);

		}

		event = new JSONObject();
		event.put("data", eventArray);

		String eventStr = event.toString();

		return callback+"("+eventStr+")";
	}

	// �̺�Ʈ �󼼺���
	@RequestMapping(value="eventOne", method=RequestMethod.GET)
	public @ResponseBody String eventOne(int bbsctt_code, HttpServletRequest request) throws Exception{


		String callback = request.getParameter("callback");
		List<HashMap> list = androidService.eventOne(bbsctt_code);

		JSONObject eventJson = new JSONObject();
		eventJson.put("bbsctt_code", list.get(0).get("bbsctt_code"));
		eventJson.put("event_begin_de", list.get(0).get("event_begin_de").toString());
		eventJson.put("event_end_de", list.get(0).get("event_end_de").toString());
		eventJson.put("bbsctt_sj", list.get(0).get("bbsctt_sj"));
		eventJson.put("bbsctt_cn", list.get(0).get("bbsctt_cn"));

		return callback + "(" + eventJson + ")";

	}



	// ���� �ٱ��Ͽ� ��� ���� ����
	@RequestMapping(value="couponList", method=RequestMethod.GET, produces = "text/plain; charset=UTF-8")
	public @ResponseBody String couponList(String user_id, HttpServletRequest request) throws Exception{

		String callback = request.getParameter("callback");
		List<CouponVO> list = androidService.couponBasket(user_id);
		JSONObject couponJson;
		JSONObject coupon;
		JSONArray couponArray = new JSONArray();
		for(int i = 0; i < list.size(); i++){
			couponJson = new JSONObject();
			couponJson.put("coupon_code", list.get(i).getCoupon_code());
			couponJson.put("coupon_nm", list.get(i).getCoupon_nm());
			couponJson.put("coupon_cntnts", list.get(i).getCoupon_cntnts());
			couponJson.put("coupon_begin_de", list.get(i).getCoupon_begin_de().toString());
			couponJson.put("coupon_end_de", list.get(i).getCoupon_end_de().toString());

			couponArray.add(couponJson);

		}
		coupon = new JSONObject();
		coupon.put("data", couponArray);

		return callback+"("+coupon+")";
	}

	// ���� �ٱ��Ͽ��� ���� ����
	@RequestMapping(value="delCouponBasket", method=RequestMethod.GET, produces = "text/plain; charset=UTF-8")
	public @ResponseBody String delCouponBasket(Coupon_holdVO coupon_holdVO, HttpServletRequest request) throws Exception{

		String callback = request.getParameter("callback");
		androidService.delCouponBasket(coupon_holdVO);


		JSONObject coupon;

		coupon = new JSONObject();
		coupon.put("result", "success");



		return callback+"("+coupon+")";
	}

	// ��꼭 ����
	@RequestMapping(value="billList", method=RequestMethod.GET, produces = "text/plain; charset=UTF-8")
	public @ResponseBody String billList(String user_id, int day, HttpServletRequest request) throws Exception{

		String callback = request.getParameter("callback");

		List<BillVO> list = androidService.billList(user_id,day);


		JSONObject billJson;

		JSONArray billArray = new JSONArray();
		for(int i=0; i < list.size(); i++){
			billJson = new JSONObject();
			billJson.put("bill_code", list.get(i).getBill_code());	
			billJson.put("bill_issu_de", list.get(i).getBill_issu_de().toString());		
			billJson.put("bill_totamt", list.get(i).getBill_totamt());

			billArray.add(billJson);
		}

		JSONObject billList = new JSONObject();
		billList.put("data", billArray);

		System.out.println(billList.toString());


		return callback+"("+billList+")";
	}

	// ��꼭 ������
	@RequestMapping(value="billOne", method=RequestMethod.GET, produces = "text/plain; charset=UTF-8")
	public @ResponseBody String billOne(int bill_code, HttpServletRequest request) throws Exception{

		String callback = request.getParameter("callback");

		List<HashMap> list = androidService.billOne(bill_code); 


		JSONObject billJson;

		JSONArray billArray = new JSONArray();
		for(int i=0; i < list.size(); i++){
			billJson = new JSONObject();	
			billJson.put("goods_nm", list.get(i).get("goods_nm"));	
			billJson.put("purchgoods_qy", list.get(i).get("purchgoods_qy"));	
			billJson.put("COUPON_DSCNT", list.get(i).get("COUPON_DSCNT"));
			billJson.put("price", list.get(i).get("price"));	

			billArray.add(billJson);
		}

		JSONObject billList = new JSONObject();
		billList.put("data", billArray);

		System.out.println(billList.toString());


		return callback+"("+billList+")";
	}

	// ��꼭�� �����ϴ� ����
	@RequestMapping(value="settleInfo", method=RequestMethod.GET, produces = "text/plain; charset=UTF-8")
	public @ResponseBody String settleInfo(String user_id, int bill_code,
			HttpServletRequest request) 
					throws Exception{

		String callback = request.getParameter("callback");

		List<HashMap> list = androidService.settleInfo(user_id, bill_code);

		JSONObject settleJson;

		JSONArray settleArray = new JSONArray();
		for(int i=0; i < list.size(); i++){
			settleJson = new JSONObject();	
			settleJson.put("bill_code", list.get(i).get("bill_code"));	
			settleJson.put("setle_mth_code", list.get(i).get("setle_mth_code"));
			settleJson.put("setle_mth_nm", list.get(i).get("setle_mth_nm"));	
			settleJson.put("stprc", list.get(i).get("stprc"));

			settleArray.add(settleJson);
		}

		JSONObject settleList = new JSONObject();
		settleList.put("data", settleArray);

		System.out.println(settleList.toString());


		return callback+"("+settleList+")";


	}

	// ��õ��ǰ
	@RequestMapping(value="recommandProduct", method=RequestMethod.GET, produces = "text/plain; charset=UTF-8")
	public @ResponseBody String recommandProduct(String user_id, HttpServletRequest request) throws Exception{

		String callback = request.getParameter("callback");

		List<HashMap> list = androidService.recommandProduct(user_id); 

		// �̺κ� �� ���� �غ���
		JSONObject recommandJSON;


		JSONArray recommandArray = new JSONArray();
		for(int i=0; i < list.size(); i++){
			recommandJSON = new JSONObject();	
			recommandJSON.put("goods_code", list.get(i).get("goods_code"));
			recommandJSON.put("goods_nm", list.get(i).get("goods_nm"));	
			recommandJSON.put("goods_pc", list.get(i).get("goods_pc"));			

			recommandArray.add(recommandJSON);
		}

		JSONObject recommandList = new JSONObject();
		recommandList.put("data", recommandArray);

		System.out.println(recommandList.toString());


		return callback+"("+recommandList+")";
	}


	// ����Ʈ
	@RequestMapping(value="point", method=RequestMethod.POST, produces = "text/plain; charset=UTF-8")
	public @ResponseBody String point(HttpServletRequest request) throws Exception{

		System.out.println(request.getParameter("user_id"));
		int point = androidService.point(request.getParameter("user_id"));
		System.out.println(point);
		return ""+point;
	}

	// ��ǰ�˻�
	@RequestMapping(value="productSearch", method=RequestMethod.POST, produces = "text/plain; charset=UTF-8")
	public @ResponseBody String productSearch(String productName, HttpServletRequest request) throws Exception{


		System.out.println(request.getParameter("productName"));

		List<GoodsVO> list = androidService.productSearch(productName);
		// �̺κ� �� ���� �غ���
		JSONObject productJSON;

		JSONArray productArray = new JSONArray();
		JSONObject productSearchList = new JSONObject();

		if(list == null){

			productArray = null;
			productSearchList.put("data", productArray);
			return productSearchList.toString();

		}



		for(int i=0; i < list.size(); i++){
			productJSON = new JSONObject();	
			productJSON.put("goods_code", list.get(i).getGoods_code());
			productJSON.put("goods_nm", list.get(i).getGoods_nm());
			productJSON.put("goods_pc", list.get(i).getGoods_pc());
			productJSON.put("goods_flpth", list.get(i).getGoods_flpth());

			productArray.add(productJSON);
		}

		productSearchList.put("data", productArray);

		System.out.println(productSearchList.toString());


		return productSearchList.toString();
	}



	@RequestMapping(value="insertCoupon_hold", method=RequestMethod.GET)
	public @ResponseBody String insertCoupon_hold(String user_id, int coupon_code, HttpServletRequest request) throws Exception{

		System.out.println(user_id + " "+coupon_code);


		String callback = request.getParameter("callback");


		androidService.insertCoupon_hold(user_id, coupon_code);

		JSONObject coupon;

		coupon = new JSONObject();
		coupon.put("result", "success");


		return callback+"("+coupon+")";


	}


	@RequestMapping(value="fcmMesseage", method=RequestMethod.POST)
	public void fcmMesseage(String userDeviceIdKey) throws Exception{

		String authKey = AUTH_KEY_FCM;
		String FCMurl = API_URL_FCM;

		URL url = new URL(FCMurl);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization", "key=" + authKey);
		conn.setRequestProperty("Content-Type", "application/json");

		JSONObject json = new JSONObject();
		JSONObject info = new JSONObject();

		info.put("body", "Ǫ�� �߼� �׽�Ʈ �Դϴ�."); // Notification body

		json.put("notification", info);
		json.put("to", userDeviceIdKey.trim()); // deviceID

		//Ȥ�ó� �ѱ� ������ �߻��ϸ�
		try(OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8")){

			wr.write(json.toString());
			wr.flush();
		}catch(Exception e){
		}

		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

		String output;
		System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}

		conn.disconnect();

	}







}
