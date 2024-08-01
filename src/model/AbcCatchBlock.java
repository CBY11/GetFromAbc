package model;/*
 *model
 *Dell
 *2024 2024/7/24 15:42
 */

public class AbcCatchBlock {
    public long type_idx; //	uleb128	值是0，表示此CatchBlock块捕获了所有类型的异常。
    public long handler_pc; //	uleb128	异常处理逻辑的第一条指令的程序计数器。
    public long code_size; //	uleb128	此CatchBlock的大小，以字节为单位。
}
