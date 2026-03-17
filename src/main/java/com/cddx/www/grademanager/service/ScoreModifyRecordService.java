package com.cddx.www.grademanager.service;

import com.cddx.www.grademanager.entity.pojo.ScoreModifyRecord;

import java.util.List;

/**
 * 成绩修改记录服务接口。
 *
 * 功能项：
 * - 提供对 score_modify_record 表的业务封装（增/查/删）
 *
 * 说明：
 * - 当前项目主要通过 Mapper 直接访问数据库；Service 用于统一异常处理与复用逻辑
 */
public interface ScoreModifyRecordService {
    // 添加成绩修改记录
    boolean addScoreModifyRecord(ScoreModifyRecord record);

    // 根据修改ID查询记录
    ScoreModifyRecord getRecordById(Integer modifyId);

    // 根据成绩ID查询修改记录列表
    List<ScoreModifyRecord> getRecordsByScoreId(Integer scoreId);

    // 根据修改人查询记录列表
    List<ScoreModifyRecord> getRecordsByModifier(String modifier);

    // 获取所有修改记录
    List<ScoreModifyRecord> getAllRecords();

    // 删除记录
    boolean deleteRecordById(Integer modifyId);
}
