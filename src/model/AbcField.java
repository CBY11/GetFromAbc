package model;

import java.util.List;

public class AbcField {
    public int class_idx;
    // 一个指向该字段从属的类的索引，指向ClassRegionIndex中的位置，
    // 该位置的值是Type类型，是一个指向Class的偏移量。
    public int type_idx;
    // 一个指向定义该字段的类型的索引，指向ClassRegionIndex中的位置，
    // 该位置的值是Type类型。
    public long name_off;
    // 一个偏移量，指向字符串，表示字段的名称。
    public String name;
    // 字段的名称。

    public long reserved;
    // 方舟字节码文件内部使用的保留字段，使用uleb128编码。
    public AbcTaggedValue[] field_data;
    // 不定长度的数组，数组中每个元素都是TaggedValue类型，
    // 元素的标记是FieldTag类型，数组中的元素按照标记递增排序（0x00标记除外）。

    public AbcField() {
    }
}
