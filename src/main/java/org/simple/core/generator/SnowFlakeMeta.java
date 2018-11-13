package org.simple.core.generator;

/**
 * snow flake 基础设置
 * Created by arebya on 18/4/8.
 */
public class SnowFlakeMeta {

    /**
     * //     * 开始时间截 (2015-01-01)
     * //
     */
    public static final long EPOCH = 1420041600000L;

    /**
     * 机器id所占的位数
     */
    public static final long WORKER_ID_BITS = 5L;

    /**
     * 数据标识id所占的位数
     */
    public static final long DATACENTER_ID_BITS = 5L;

    /**
     * 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
     */
    public static final long MAX_WORKER_ID = -1L ^ (-1L << WORKER_ID_BITS);

    /**
     * 支持的最大数据标识id，结果是31
     */
    public static final long MAX_DATACENTER_ID = -1L ^ (-1L << DATACENTER_ID_BITS);

    /**
     * 序列在id中占的位数
     */
    public static final long SEQUENCE_BITS = 12L;

    /**
     * 机器ID向左移12位
     */
    public static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

    /**
     * 数据标识id向左移17位(12+5)
     */
    public static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    /**
     * 时间截向左移22位(5+5+12)
     */
    public static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
     */
    public static final long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_BITS);
}
