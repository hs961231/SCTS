package yjc.wdb.scts.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import yjc.wdb.scts.bean.BillVO;
import yjc.wdb.scts.bean.CouponVO;
import yjc.wdb.scts.bean.Coupon_holdVO;
import yjc.wdb.scts.bean.GoodsVO;
import yjc.wdb.scts.bean.UserVO;
import yjc.wdb.scts.dao.AndroidDAO;
import yjc.wdb.scts.service.AndroidService;

@Service
public class AndroidServiceImpl implements AndroidService{
	
	@Inject
	private AndroidDAO dao;
	
	@Override
	public List<BillVO> billList(String user_id, int day) throws Exception {
		
		return dao.billList(user_id, day);
	}

	@Override
	public List<HashMap> billOne(int bill_code) throws Exception {
		
		return dao.billOne(bill_code);
	}

	@Override
	public List<HashMap> settleInfo(String user_id, int bill_code) throws Exception {
		
		return dao.settleInfo(user_id, bill_code);
	}

	@Override
	public List<HashMap> recommandProduct(String user_id) throws Exception {
		
		return dao.recommandProduct(user_id);
	}
	
	
	@Override
	public List<CouponVO> couponBasket(String user_id) throws Exception {
		
		return dao.couponBasket(user_id);
	}

	@Override
	public void delCouponBasket(Coupon_holdVO coupon_holdVO) throws Exception {
		
		dao.delCouponBasket(coupon_holdVO);
		
	}
	
	@Override
	@Transactional
	public CouponVO selectSendAndroidCoupon(Map<String, String> vo) throws Exception {
		// ���� ������ Ÿ�Ͽ��� ���� �� �ִ� �������� �˻�
		List<HashMap<String, Integer>> coupon_code_Array = dao.TileCoupon_code(vo);
		
		// �ش� �������� ����
		Map<String, Object> map = new HashMap<String, Object>();

		if(coupon_code_Array.isEmpty())
			return null;
		
		map.put("user_id", vo.get("user_id"));
		map.put("coupon_code_Array", coupon_code_Array);
		
		// ����� ������ �� ���� ���� �ִ��� �˻�
		int coupon = dao.scanCoupon_hold(map);
		
		System.out.println("���⼭ ��� �ǳ� coupon �� = " + coupon);
		// ������ ���� ��� ������ ���� ����
		if(coupon > 0)
			return null;
		
		// ���� �� ������ ������ �˻��ؼ� �������� �Ѱ� ��
		return dao.selectSendAndroidCoupon(map);
	}

	@Override
	public List<GoodsVO> productSearch(String productName) throws Exception {
		
		return dao.productSearch(productName);
	}

	@Override
	public List<HashMap> eventList() throws Exception {
		// TODO Auto-generated method stub
		return dao.eventList();
	}

	@Override
	public List<HashMap> eventOne(int bbsctt) throws Exception {
		// TODO Auto-generated method stub
		return dao.eventOne(bbsctt);
	}

	@Override
	public void insertCoupon_hold(String user_id, int coupon_code) throws Exception {
		dao.insertCoupon_hold(user_id, coupon_code);
		
	}

	@Override
	public void updateToken(UserVO user) throws Exception {
		
		dao.updateToken(user);
				
	}

	@Override
	public int androidLoginUser(UserVO user) throws Exception {
		
		return dao.androidLoginUser(user);
	}
	
	
	@Override
	public int checkUser(String id) throws Exception {
		
		return dao.checkUser(id);
	}

	@Override
	public int point(String user_id) throws Exception {
		
		return dao.point(user_id);
	}

	@Override
	public String userToken(String user_id) throws Exception {
		
		return dao.userToken(user_id);
	}

	@Override
	public List<HashMap> fcmCoupon(String user_id) throws Exception {
		
		return dao.fcmCoupon(user_id);
	}

	@Override
	public void periodicCoupon(String user_id, int coupon_code) throws Exception {
		
		int count = dao.confirmCoupon(user_id, coupon_code);
		
		if(count == 0){
			dao.insertCoupon_hold(user_id, coupon_code);
		}
		
	}

	@Override
	public List<HashMap> basketInfo(String user_id, int bhf_code) throws Exception {
	
		return dao.basketInfo(user_id, bhf_code);
	}

	public void updateBasket_qy(JSONObject obj) throws Exception {
		
		dao.updateBasket_qy(obj);
		
	}

	@Transactional
	@Override
	public List<HashMap> basket(JSONObject obj) throws Exception {
		
		String user_id = obj.get("user_id").toString();
		int bhf_code = Integer.parseInt(obj.get("bhf_code").toString());
		
		List<HashMap> list = dao.basketInfo(user_id, bhf_code);
		
		int count = 0;
		for(int i = 0; i < list.size(); i++){
			if((list.get(i).get("goods_code").toString()).equals(obj.get("goods_code").toString())){
				
				System.out.println("�����մϴ�!!!!"+list.get(i).get("goods_code").toString());
				System.out.println(obj.get("goods_code").toString());
				
				count++;
				
				
			}
		}
		
		if(count <= 0){
			dao.insertBasket(obj);
		}else{
			int basket_qy = dao.knowBasket_qy(obj) +1;
			
			obj.put("basket_qy", basket_qy);
	
			dao.updateBasket_qy(obj);
		}
		
		
		return dao.oneBasketInfo(obj);
	}

	@Override
	public void delBasket(int bhf_code, int goods_code, String user_id) throws Exception {
		dao.delBasket(bhf_code, goods_code, user_id);
	}

	@Override
	public String userDeliveryAddr(String user_id) throws Exception {
		
		return dao.userDeliveryAddr(user_id);
	}
	


}
