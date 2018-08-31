package com.zhuanche.dto;

import java.util.List;

public class SaasPermissionDTO {
    private Integer permissionId;

    private Integer parentPermissionId;

    private String permissionCode;

    private Byte permissionType;

    private String permissionName;

    private String menuUrl;

    private Byte menuOpenMode;

    private Boolean valid;

    private Integer sortSeq;

    
    /**子权限**/
    private List<SaasPermissionDTO> childPermissions;
	public List<SaasPermissionDTO> getChildPermissions() {
		return childPermissions;
	}
	public void setChildPermissions(List<SaasPermissionDTO> childPermissions) {
		this.childPermissions = childPermissions;
	}

	/**是否选中 (便于UI操作)**/
	private boolean checked = false;
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	
	
	public Integer getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }

    public Integer getParentPermissionId() {
        return parentPermissionId;
    }

    public void setParentPermissionId(Integer parentPermissionId) {
        this.parentPermissionId = parentPermissionId;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode == null ? null : permissionCode.trim();
    }

    public Byte getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(Byte permissionType) {
        this.permissionType = permissionType;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName == null ? null : permissionName.trim();
    }

    public String getMenuUrl() {
        return menuUrl;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl == null ? null : menuUrl.trim();
    }

    public Byte getMenuOpenMode() {
        return menuOpenMode;
    }

    public void setMenuOpenMode(Byte menuOpenMode) {
        this.menuOpenMode = menuOpenMode;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Integer getSortSeq() {
        return sortSeq;
    }

    public void setSortSeq(Integer sortSeq) {
        this.sortSeq = sortSeq;
    }
}