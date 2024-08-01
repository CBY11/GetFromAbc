package model;/*
 *model
 *Dell
 *2024 2024/7/25 10:31
 */

public class AbcType {
    public boolean isBase = true;
    public long code_offset;
    public AbcClass abcClass;
}

// 类型	编码
// u1	    0x00
// i8	    0x01
// u8	    0x02
// i16	    0x03
// u16	    0x04
// i32	    0x05
// u32	    0x06
// f32	    0x07
// f64	    0x08
// i64	    0x09
// u64	    0x0a
// any	    0x0c