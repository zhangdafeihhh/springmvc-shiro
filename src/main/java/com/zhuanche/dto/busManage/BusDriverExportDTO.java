package com.zhuanche.dto.busManage;

/**
 * @ClassName: BusDriverQueryDTO
 * @Description: 巴士司机导出DTO
 * @author: yanyunpeng
 * @date: 2018年11月22日 下午4:23:15
 * 
 */
public class BusDriverExportDTO extends BusDriverQueryDTO {

	private static final long serialVersionUID = 4494401825119191743L;

	@Override
	public void setPageNum(Integer pageNum) {
		super.pageNum = pageNum;
	}

	@Override
	public void setPageSize(Integer pageSize) {
		super.pageSize = pageSize;
	}

}
