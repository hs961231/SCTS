package yjc.wdb.scts.service;

import java.util.HashMap;
import java.util.List;

import yjc.wdb.scts.bean.TileVO;

public interface TileService {
	public List<HashMap<String, String>> selectTileList() throws Exception; // ������ ������
	public List<HashMap<String, String>> selectTileListUp() throws Exception; // ���� ����
	public void insertTile(TileVO vo) throws Exception;
	public HashMap<String, String> selectTile_LocationOne(TileVO vo) throws Exception;
	public void updateTileBeaconSet(HashMap<String, String> vo) throws Exception;

}
