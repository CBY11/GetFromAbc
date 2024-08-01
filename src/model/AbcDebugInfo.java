package model;/*
 *model
 *Dell
 *2024 2024/7/24 14:53
 */

public class AbcDebugInfo {

    public long line_start;//	uleb128	状态机的行号寄存器的初始值。
    public long num_parameters;//	uleb128	入参和默认参数的总数量。
    public long[] parameters;//		uleb128[]	存放方法入参的名称的数组，数组长度是num_parameters。每一个元素的值是字符串的偏移量或者0，如果是0，则代表对应的参数不具有名称。
    public long constant_pool_size;//	uleb128	常量池的大小，以字节为单位。
    public long[] constant_pool;//	uleb128[]	存放常量池数据的数组，数组长度是constant_pool_size。
    public long line_number_program_idx;//	uleb128	一个索引，指向一个在LineNumberProgramIndex中的位置，该位置的值是一个指向Line number program的偏移量。Line number program的长度可变，以END_SEQUENCE操作码结束。

}
