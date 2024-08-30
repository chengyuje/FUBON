package com.systex.jbranch.app.server.fps.org131;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class ORG131OutputVO extends PagingOutputVO {
		private List resultList;
		private List resultList2;
		private List resultList3;
		private List resultList4;
		private List resultList5;
		private List resultList6;
		private List csvList;   //csv
		private List list;
		private List resultList7;
		private List resultList8;
		private List resultList9;
		private List roleID;
		private List reviewdate;
		private List rewcust;
		
		private List rcList;
		private List opList;
		private boolean isSupervisor;
		private boolean isHeadMgr;
		
		private String errorMsg;
		
		
		
		public String getErrorMsg() {
			return errorMsg;
		}

		public void setErrorMsg(String errorMsg) {
			this.errorMsg = errorMsg;
		}

		public List getRcList() {
			return rcList;
		}

		public void setRcList(List rcList) {
			this.rcList = rcList;
		}

		public List getOpList() {
			return opList;
		}

		public void setOpList(List opList) {
			this.opList = opList;
		}

		public List getCsvList() {
			return csvList;
		}

		public List getList() {
			return list;
		}

		public void setList(List list) {
			this.list = list;
		}

		public void setCsvList(List csvList) {
			this.csvList = csvList;
		}

		public List getResultList() {
			return resultList;
		}

		public void setResultList(List resultList) {
			this.resultList = resultList;
		}

		public List getResultList2() {
			return resultList2;
		}

		public void setResultList2(List resultList2) {
			this.resultList2 = resultList2;
		}

		public List getResultList3() {
			return resultList3;
		}

		public void setResultList3(List resultList3) {
			this.resultList3 = resultList3;
		}

		public List getResultList4() {
			return resultList4;
		}

		public void setResultList4(List resultList4) {
			this.resultList4 = resultList4;
		}

		public List getResultList5() {
			return resultList5;
		}

		public void setResultList5(List resultList5) {
			this.resultList5 = resultList5;
		}

		public List getResultList6() {
			return resultList6;
		}

		public void setResultList6(List resultList6) {
			this.resultList6 = resultList6;
		}

		public List getResultList7() {
			return resultList7;
		}

		public void setResultList7(List resultList7) {
			this.resultList7 = resultList7;
		}

		public List getResultList8() {
			return resultList8;
		}

		public void setResultList8(List resultList8) {
			this.resultList8 = resultList8;
		}

		public List getResultList9() {
			return resultList9;
		}

		public void setResultList9(List resultList9) {
			this.resultList9 = resultList9;
		}

		public List getRoleID() {
			return roleID;
		}

		public void setRoleID(List roleID) {
			this.roleID = roleID;
		}

		public List getReviewdate() {
			return reviewdate;
		}

		public void setReviewdate(List reviewdate) {
			this.reviewdate = reviewdate;
		}

		public List getRewcust() {
			return rewcust;
		}

		public void setRewcust(List rewcust) {
			this.rewcust = rewcust;
		}

		public boolean isSupervisor() {
			return isSupervisor;
		}

		public void setSupervisor(boolean isSupervisor) {
			this.isSupervisor = isSupervisor;
		}

		public boolean isHeadMgr() {
			return isHeadMgr;
		}

		public void setHeadMgr(boolean isHeadMgr) {
			this.isHeadMgr = isHeadMgr;
		}

		
		
}
