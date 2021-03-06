package com.pinterest.arcee.dao;


import com.pinterest.arcee.bean.ManagingGroupsBean;
import java.util.List;

public interface ManagingGroupDAO {
    void insertManagingGroup(String clusterName, ManagingGroupsBean managingGroupsBean) throws Exception;

    ManagingGroupsBean getManagingGroupByGroupName(String groupName) throws Exception;

    void updateManagingGroup(String groupName, ManagingGroupsBean managingGroupsBean) throws Exception;

    List<ManagingGroupsBean> getLendManagingGroupsByInstanceType(String instanceType) throws Exception;

    List<ManagingGroupsBean> getReturnManagingGroupsByInstanceType(String instanceType) throws Exception;

    void deleteManagingGroup(String groupName) throws Exception;
}
