package yjc.wdb.scts.dao;

import java.util.Map;

/* ���� ���ܰ� 
 * ���� ������ �Բ� ó���ϴ� dao
 */
public interface Settlement_methodDAO {
	public void insertSettlement_method(String setle_mth_nm) throws Exception;
	public void insertSettlement_infomation(Map<String, String> map) throws Exception;
}
