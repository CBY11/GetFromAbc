package model;/*
 *model
 *Dell
 *2024 2024/7/24 14:39
 */

public class AbcCode {
    public long num_vregs;	//uleb128	寄存器的数量，存放入参和默认参数的寄存器不计算在内。
    public long num_args;	//uleb128	入参和默认参数的总数量。
    public long code_size;	//uleb128	所有指令的总大小，以字节为单位。
    public long tries_size;	//	uleb128	try_blocks数组的长度，即TryBlock的数量。
    public byte[] instructions;	//	uint8_t[]	所有指令的数组。
    public String insns_code;	//	string	所有指令的字符串形式。
    public AbcTryBlock[] try_blocks;	//	TryBlock[]	一个数组，数组中每一个元素都是TryBlock类型。
}
