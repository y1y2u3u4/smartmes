package com.smartmes.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果封装类
 *
 * @author SmartMES
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页记录数
     */
    private Integer pageSize;

    /**
     * 总页数
     */
    private Integer totalPages;

    /**
     * 构造分页结果
     *
     * @param records 数据列表
     * @param total 总记录数
     * @param pageNum 当前页码
     * @param pageSize 每页记录数
     */
    public PageResult(List<T> records, Long total, Integer pageNum, Integer pageSize) {
        this.records = records;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }

    /**
     * 创建空的分页结果
     *
     * @param pageNum 当前页码
     * @param pageSize 每页记录数
     * @param <T> 数据类型
     * @return 空分页结果
     */
    public static <T> PageResult<T> empty(Integer pageNum, Integer pageSize) {
        return new PageResult<>(List.of(), 0L, pageNum, pageSize, 0);
    }

    /**
     * 创建分页结果
     *
     * @param records 数据列表
     * @param total 总记录数
     * @param pageNum 当前页码
     * @param pageSize 每页记录数
     * @param <T> 数据类型
     * @return 分页结果
     */
    public static <T> PageResult<T> of(List<T> records, Long total, Integer pageNum, Integer pageSize) {
        return new PageResult<>(records, total, pageNum, pageSize);
    }

    /**
     * 判断是否有上一页
     *
     * @return true-有上一页，false-无上一页
     */
    public boolean hasPrevious() {
        return this.pageNum > 1;
    }

    /**
     * 判断是否有下一页
     *
     * @return true-有下一页，false-无下一页
     */
    public boolean hasNext() {
        return this.pageNum < this.totalPages;
    }

    /**
     * 判断是否为第一页
     *
     * @return true-是第一页，false-不是第一页
     */
    public boolean isFirst() {
        return this.pageNum == 1;
    }

    /**
     * 判断是否为最后一页
     *
     * @return true-是最后一页，false-不是最后一页
     */
    public boolean isLast() {
        return this.pageNum.equals(this.totalPages);
    }
}
