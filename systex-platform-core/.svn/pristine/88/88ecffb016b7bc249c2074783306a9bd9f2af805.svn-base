package com.systex.jbranch.platform.common.security.module;


import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TbsyssecuitemfunVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsyssecumodVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsyssecumoduitemVO;
import com.systex.jbranch.platform.common.security.module.vo.ItemFunctionVO;
import com.systex.jbranch.platform.common.security.module.vo.ModuleItemVO;
import com.systex.jbranch.platform.common.security.module.vo.ModuleVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 功能模組管理類別，可對功能模組、模組交易、交易細項功能作新增修改刪除與查詢
 *
 * @author Hong-jie
 * @version 1.0 2008/4/14
 */
public class ModuleManagement {
// ------------------------------ FIELDS ------------------------------

    DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(ModuleManagement.class);

// --------------------------- CONSTRUCTORS ---------------------------

    public ModuleManagement() throws DAOException, JBranchException {
        dam = new DataAccessManager();
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * 新增交易細項功能
     *
     * @param functionVO
     */
    public void createItemFunction(com.systex.jbranch.platform.common.security.module.vo.ItemFunctionVO functionVO) {
        try {
            TbsyssecuitemfunVO vo = new TbsyssecuitemfunVO();
            vo.setFunctionid(functionVO.getFunctionid());
            vo.setApply(functionVO.getApply());
            vo.setDescription(functionVO.getDescription());
            vo.setName(functionVO.getFunctionname());
            /*
               TbsyssecuitemfunVO.setCreatetime(new Timestamp(System.currentTimeMillis()));
               TbsyssecuitemfunVO.setLastupdate(new Timestamp(System.currentTimeMillis()));
               TbsyssecuitemfunVO.setCreator("SYSTEM");
               TbsyssecuitemfunVO.setModifier("SYSTEM");
               */
            dam.create(vo);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 新增功能模組
     *
     * @param moduleVO
     */
    public void createModule(com.systex.jbranch.platform.common.security.module.vo.ModuleVO moduleVO) {
        try {
            TbsyssecumodVO vo = new TbsyssecumodVO();
            vo.setName(moduleVO.getModulename());
            vo.setModuleid(moduleVO.getModuleid());
            vo.setApply(moduleVO.getApply());
            vo.setDescription(moduleVO.getDescription());
            /*
               TbsyssecumodVO.setCreatetime(new Timestamp(System.currentTimeMillis()));
               TbsyssecumodVO.setLastupdate(new Timestamp(System.currentTimeMillis()));
               TbsyssecumodVO.setCreator("SYSTEM");
               TbsyssecumodVO.setModifier("SYSTEM");
               */
            dam.create(vo);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 新增模組交易
     *
     * @param itemVO
     */
    public void createModuleItem(com.systex.jbranch.platform.common.security.module.vo.ModuleItemVO itemVO) {
        try {
//			TbsyssecumoduitemVO vo=new TbsyssecumoduitemVO();
//			vo.setItemid(itemVO.getItemid());
//			vo.setApply(itemVO.getApply());
//			vo.setDescription(itemVO.getDescription());
//			vo.setName(itemVO.getItemname());
//			/*
//			TbsyssecumoduitemVO.setCreatetime(new Timestamp(System.currentTimeMillis()));
//			TbsyssecumoduitemVO.setLastupdate(new Timestamp(System.currentTimeMillis()));
//			TbsyssecumoduitemVO.setCreator("SYSTEM");
//			TbsyssecumoduitemVO.setModifier("SYSTEM");
//			*/
//			dam.create(vo);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 取得所有功能模組
     *
     * @return Collection<ModuleVO>
     */
    public java.util.Collection getAllModule() {
        Set<TbsyssecumodVO> set = new HashSet();
        try {
            Iterator<TbsyssecumodVO> it = dam.findAll(TbsyssecumodVO.TABLE_UID).iterator();
            while (it.hasNext()) {
                set.add(it.next());
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return set;
    }

    /**
     * 取得交易細項功能
     *
     * @param functionID
     * @return ItemFunctionVO
     */
    public com.systex.jbranch.platform.common.security.module.vo.ItemFunctionVO getItemFunction(String functionID) {
        ItemFunctionVO itemFunctionVO = new ItemFunctionVO();
        try {
            TbsyssecuitemfunVO vo = (TbsyssecuitemfunVO) dam.findByPKey(TbsyssecuitemfunVO.TABLE_UID, functionID);
            itemFunctionVO.setFunctionid(vo.getFunctionid());
            itemFunctionVO.setDescription(vo.getDescription());
            itemFunctionVO.setApply(vo.getApply());
            itemFunctionVO.setFunctionname(vo.getName());
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return itemFunctionVO;
    }

    /**
     * 取得功能模組
     *
     * @param moduleID
     * @reutrn moduleVO
     */
    public com.systex.jbranch.platform.common.security.module.vo.ModuleVO getModule(String moduleID) {
        ModuleVO moduleVO = new ModuleVO();
        try {
            TbsyssecumodVO vo = (TbsyssecumodVO) dam.findByPKey(TbsyssecumodVO.TABLE_UID, moduleID);
            moduleVO.setModuleid(vo.getModuleid());
            moduleVO.setDescription(vo.getDescription());
            moduleVO.setApply(vo.getApply());
            moduleVO.setModulename(vo.getName());
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return moduleVO;
    }

    /**
     * 取得模組交易
     *
     * @param itemID
     * @return moduleItemVO
     */
    public com.systex.jbranch.platform.common.security.module.vo.ModuleItemVO getModuleItem(String itemID) {
        ModuleItemVO moduleItemVO = new ModuleItemVO();
        try {
//			TbsyssecumoduitemVO vo= (TbsyssecumoduitemVO) dam.findByPKey(TbsyssecumoduitemVO.TABLE_UID,itemID);
//			moduleItemVO.setItemid(vo.getItemid());
//			moduleItemVO.setDescription(vo.getDescription());
//			moduleItemVO.setApply(vo.getApply());
//			moduleItemVO.setItemname(vo.getName());
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return moduleItemVO;
    }

    /**
     * 取得指定功能模組下的所有模組交易
     *
     * @param moduleID
     * @return Collection<ModuleItemVO>
     */
    public ModuleItemVO getModuleItemByModule(String moduleID) {
        ModuleItemVO moduleItemVO = new ModuleItemVO();
        try {
//			TbsyssecumoduitemVO vo= (TbsyssecumoduitemVO) dam.findByPKey(TbsyssecumoduitemVO.TABLE_UID, moduleID);
//    		moduleItemVO.setItemid(vo.getItemid());
//    		moduleItemVO.setApply(vo.getApply());
//    		moduleItemVO.setDescription(vo.getDescription());
//    		moduleItemVO.setItemname(vo.getName());
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return moduleItemVO;
    }

    /**
     * 移除交易細項功能
     *
     * @param functionID
     */
    public void removeItemFunction(String functionID) {
        try {
            TbsyssecuitemfunVO vo = (TbsyssecuitemfunVO) dam.findByPKey(TbsyssecuitemfunVO.TABLE_UID, functionID);
            dam.delete(vo);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 移除功能模組
     *
     * @param moduleID
     */
    public void removeModule(String moduleID) {
        try {
            TbsyssecumodVO vo = (TbsyssecumodVO) dam.findByPKey(TbsyssecumodVO.TABLE_UID, moduleID);
            dam.delete(vo);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 移除模組交易
     *
     * @param itemID
     */
    public void removeModuleItem(String itemID) {
        try {
            TbsyssecumoduitemVO vo = new TbsyssecumoduitemVO();
            vo = (TbsyssecumoduitemVO) dam.findByPKey(TbsyssecumoduitemVO.TABLE_UID, itemID);
            dam.delete(vo);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 更新交易細項功能
     *
     * @param function
     */
    public void updateItemFunction(com.systex.jbranch.platform.common.security.module.vo.ItemFunctionVO function) {
        try {
            TbsyssecuitemfunVO vo = new TbsyssecuitemfunVO();
            String functionid = function.getFunctionid();
            vo = (TbsyssecuitemfunVO) dam.findByPKey(TbsyssecuitemfunVO.TABLE_UID, functionid);

            vo.setName(function.getFunctionname());
            vo.setDescription(function.getDescription());
            vo.setApply(function.getApply());

            /*
               TbsyssecuitemfunVO.setCreator("SYSTEM");
               TbsyssecuitemfunVO.setLastupdate(new Timestamp(System.currentTimeMillis()));
               TbsyssecuitemfunVO.setModifier("SYSTEM");
               */
            dam.update(vo);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 更新功能模組
     *
     * @param moduleVO
     */
    public void updateModule(com.systex.jbranch.platform.common.security.module.vo.ModuleVO module) {
        try {
            TbsyssecumodVO vo = new TbsyssecumodVO();
            String moduleID = module.getModuleid();
            vo = (TbsyssecumodVO) dam.findByPKey(TbsyssecumodVO.TABLE_UID, moduleID);
            vo.setName(module.getModulename());
            vo.setDescription(module.getDescription());
            vo.setApply(module.getApply());
            /*
               TbsyssecumodVO.setCreator("SYSTEM");
               TbsyssecumodVO.setLastupdate(new Timestamp(System.currentTimeMillis()));
               TbsyssecumodVO.setModifier("SYSTEM");
               */
            dam.update(vo);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 更新模組交易
     *
     * @param moduleItem
     */
    public void updateModuleItem(com.systex.jbranch.platform.common.security.module.vo.ModuleItemVO moduleItem) {
        try {
//			TbsyssecumoduitemVO vo= new TbsyssecumoduitemVO();
//			String itemID=moduleItem.getItemid();
//			vo=(TbsyssecumoduitemVO) dam.findByPKey(TbsyssecumoduitemVO.TABLE_UID,itemID);
//			vo.setName(moduleItem.getItemid());
//			vo.setDescription(moduleItem.getDescription());
//			vo.setApply(moduleItem.getApply());

            /*
               TbsyssecumoduitemVO.setCreator("SYSTEM");
               TbsyssecumoduitemVO.setLastupdate(new Timestamp(System.currentTimeMillis()));
               TbsyssecumoduitemVO.setModifier("SYSTEM");
               */
//			dam.update(vo);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
