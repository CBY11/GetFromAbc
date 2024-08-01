package model;/*
 *model
 *Dell
 *2024 2024/7/24 14:46
 */

public class AbcTryBlock {
    public long start_pc; //	uleb128	TryBlock的第一条指令距离其所在Code的instructions的起始位置的偏移量。
    public long length; //	uleb128	TryBlock的大小，以字节为单位。
    public long num_catches; //	uleb128	与TryBlock关联的CatchBlock的数量，值为1。
    public AbcCatchBlock[] catch_blocks; //	CatchBlock[]	与TryBlock关联的CatchBlock的数组，数组中有且仅有一个可以捕获所有类型的异常的CatchBlock。
}
