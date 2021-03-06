package yjc.wdb.scts.dao;

import java.util.List;

import yjc.wdb.scts.bean.HelpVO;
import yjc.wdb.scts.bean.PageVO;

public interface HelpDAO {
	public void createHelp(HelpVO vo)throws Exception;
	public void createHelp2(HelpVO vo)throws Exception;
	public void updateHelp(HelpVO vo)throws Exception;
	public void updateHelp2(HelpVO vo)throws Exception;
	public void deleteHelp(Integer bbsctt_code)throws Exception;
	public int maxHelp()throws Exception;
	public HelpVO readHelp(Integer bbsctt_code)throws Exception;
	public List<HelpVO> listSearch(PageVO cri) throws Exception; /* 검색용 리스트 */
	public int countSearch(PageVO cri) throws Exception; // 검색용 totalCount
}
