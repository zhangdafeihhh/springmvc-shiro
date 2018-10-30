package com.zhuanche.web.entity.driver;
import com.zhuanche.web.entity.common.BaseEntity;


/***
 * dirverInfoDetail
 * @author
 */
public class DriverInfoDetailEntity extends BaseEntity {

	private static final long serialVersionUID = 7868838087731509604L;
	private Integer id;
	private Integer driverId;
	private String bankCardBank;
	private String bankCardNumber;
	private Integer ext1;
	private Integer ext2;
	private Integer ext3;
	private Integer ext4;
	private Integer ext5;
	private String ext6;
	private String ext7;
	private String ext8;
	private String ext9;
	private String ext10;
	private String createDate;
	private String updateDate;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDriverId() {
		return driverId;
	}

	public void setDriverId(Integer driverId) {
		this.driverId = driverId;
	}

	public String getBankCardBank() {
		return bankCardBank;
	}

	public void setBankCardBank(String bankCardBank) {
		this.bankCardBank = bankCardBank;
	}

	public String getBankCardNumber() {
		return bankCardNumber;
	}

	public void setBankCardNumber(String bankCardNumber) {
		this.bankCardNumber = bankCardNumber;
	}

	public Integer getExt1() {
		return ext1;
	}

	public void setExt1(Integer ext1) {
		this.ext1 = ext1;
	}

	public Integer getExt2() {
		return ext2;
	}

	public void setExt2(Integer ext2) {
		this.ext2 = ext2;
	}

	public Integer getExt3() {
		return ext3;
	}

	public void setExt3(Integer ext3) {
		this.ext3 = ext3;
	}

	public Integer getExt4() {
		return ext4;
	}

	public void setExt4(Integer ext4) {
		this.ext4 = ext4;
	}

	public Integer getExt5() {
		return ext5;
	}

	public void setExt5(Integer ext5) {
		this.ext5 = ext5;
	}

	public String getExt6() {
		return ext6;
	}

	public void setExt6(String ext6) {
		this.ext6 = ext6;
	}

	public String getExt7() {
		return ext7;
	}

	public void setExt7(String ext7) {
		this.ext7 = ext7;
	}

	public String getExt8() {
		return ext8;
	}

	public void setExt8(String ext8) {
		this.ext8 = ext8;
	}

	public String getExt9() {
		return ext9;
	}

	public void setExt9(String ext9) {
		this.ext9 = ext9;
	}

	public String getExt10() {
		return ext10;
	}

	public void setExt10(String ext10) {
		this.ext10 = ext10;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
}
