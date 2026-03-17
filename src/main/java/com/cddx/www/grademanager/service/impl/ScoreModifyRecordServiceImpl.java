package com.cddx.www.grademanager.service.impl;

import com.cddx.www.grademanager.entity.pojo.ScoreModifyRecord;
import com.cddx.www.grademanager.mapper.ScoreModifyRecordMapper;
import com.cddx.www.grademanager.service.ScoreModifyRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 成绩修改记录服务实现类（业务层）。
 *
 * 功能项：
 * - 写入修改记录（通常在成绩被修改时调用）
 * - 查询某成绩的修改历史
 * - 查询某修改人的操作历史
 */
@Service
public class ScoreModifyRecordServiceImpl implements ScoreModifyRecordService {

    @Autowired
    private ScoreModifyRecordMapper scoreModifyRecordMapper;

    @Override
    public boolean addScoreModifyRecord(ScoreModifyRecord record) {
        try {
            // 插入修改记录（modify_id 自增）
            return scoreModifyRecordMapper.insertScoreModifyRecord(record) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ScoreModifyRecord getRecordById(Integer modifyId) {
        return scoreModifyRecordMapper.selectRecordById(modifyId);
    }

    @Override
    public List<ScoreModifyRecord> getRecordsByScoreId(Integer scoreId) {
        return scoreModifyRecordMapper.selectRecordsByScoreId(scoreId);
    }

    @Override
    public List<ScoreModifyRecord> getRecordsByModifier(String modifier) {
        return scoreModifyRecordMapper.selectRecordsByModifier(modifier);
    }

    @Override
    public List<ScoreModifyRecord> getAllRecords() {
        return scoreModifyRecordMapper.selectAllRecords();
    }

    @Override
    public boolean deleteRecordById(Integer modifyId) {
        try {
            // 按主键删除一条修改记录
            return scoreModifyRecordMapper.deleteRecordById(modifyId) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
