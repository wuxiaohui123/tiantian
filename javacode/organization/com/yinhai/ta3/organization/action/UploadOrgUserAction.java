package com.yinhai.ta3.organization.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.util.ExcelFileUtils;
import com.yinhai.sysframework.util.ReflectUtil;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.organization.service.IUploadOrgUserService;
import com.yinhai.ta3.system.org.domain.UploadOrgUserVO;

public class UploadOrgUserAction extends OrgBaseAction {

	private IUploadOrgUserService uploadOrgUserService = (IUploadOrgUserService) super.getService("uploadOrgUserService");
	private File upload;
	private String uploadFileName;
	private File uploadOrg;
	private String uploadOrgFileName;
	private File uploadUser;
	private String uploadUserFileName;

	public String execute() throws Exception {
		setData("uploadSm",
				"1.分三种导入方式，按需求导入。\n2.导入组织和人员既包括组织也包括人员，导入格式见database下的《组织人员批量导入.xls》。\n3.导入组织只批量导入组织，导入格式见database下的《组织批量导入.xls》。\n4.导入人员只批量导入人员，导入人员时必须先选择一个组织，导入格式见database下的《人员批量导入.xls》。\n5.导入前请认真阅读database下的《批量导入组织和人员说明.docx》");
		return super.execute();
	}

	public String detachUploadOrgAndUser() throws Exception {
		if (ValidateUtil.isNotEmpty(uploadFileName)) {
			if (uploadFileName.toLowerCase().endsWith(".xls")) {
				InputStream fin = new FileInputStream(upload.getPath());
				String vo = SysConfig.getSysConfig("UploadOrgUserVO", UploadOrgUserVO.class.getName());
				UploadOrgUserVO v = (UploadOrgUserVO) ReflectUtil.newInstance(vo);
				List<UploadOrgUserVO> list = ExcelFileUtils.getExcelInputStream2ObjectList(fin, v.getOrgUserFieldsStr(), vo, true);
				uploadOrgUserService.detachUploadOrgAndUser(list, getDto());
				setMsg("上传成功");
			} else {
				setMsg("请上传以xls结尾的excel文件");
			}
		} else {
			setMsg("上传失败");
		}
		return "tofile";
	}

	public String detachUploadOrg() throws Exception {
		if (ValidateUtil.isNotEmpty(uploadOrgFileName)) {
			if (uploadOrgFileName.toLowerCase().endsWith(".xls")) {
				InputStream fin = new FileInputStream(uploadOrg.getPath());
				String vo = SysConfig.getSysConfig("UploadOrgUserVO", UploadOrgUserVO.class.getName());
				UploadOrgUserVO v = (UploadOrgUserVO) ReflectUtil.newInstance(vo);
				List<UploadOrgUserVO> list = ExcelFileUtils.getExcelInputStream2ObjectList(fin, v.getOrgFieldsStr(), vo, true);
				uploadOrgUserService.detachUploadOrg(list, getDto());
				setMsg("上传成功");
			} else {
				setMsg("请上传以xls结尾的excel文件");
			}
		} else {
			setMsg("上传失败");
		}
		return "tofile";
	}

	public String detachUploadUser() throws Exception {
		if (ValidateUtil.isNotEmpty(uploadUserFileName)) {
			if (uploadUserFileName.toLowerCase().endsWith(".xls")) {
				InputStream fin = new FileInputStream(uploadUser.getPath());
				String vo = SysConfig.getSysConfig("UploadOrgUserVO", UploadOrgUserVO.class.getName());
				UploadOrgUserVO v = (UploadOrgUserVO) ReflectUtil.newInstance(vo);
				try {
					List<UploadOrgUserVO> list = ExcelFileUtils.getExcelInputStream2ObjectList(fin, v.getUserFieldsStr(), vo, true);
					uploadOrgUserService.detachUploadUser(list, getDto());
					setMsg("上传成功");
				} catch (Exception e) {
					setSuccess(false);
					setMsg("上传失败，请检查上传文件的内容格式");
				}
			} else {
				setMsg("请上传以xls结尾的excel文件");
			}
		} else {
			setMsg("上传失败");
		}
		return "tofile";
	}

	public File getUploadOrg() {
		return uploadOrg;
	}

	public void setUploadOrg(File uploadOrg) {
		this.uploadOrg = uploadOrg;
	}

	public String getUploadOrgFileName() {
		return uploadOrgFileName;
	}

	public void setUploadOrgFileName(String uploadOrgFileName) {
		this.uploadOrgFileName = uploadOrgFileName;
	}

	public File getUploadUser() {
		return uploadUser;
	}

	public void setUploadUser(File uploadUser) {
		this.uploadUser = uploadUser;
	}

	public String getUploadUserFileName() {
		return uploadUserFileName;
	}

	public void setUploadUserFileName(String uploadUserFileName) {
		this.uploadUserFileName = uploadUserFileName;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}
}
