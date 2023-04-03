package prettypop.shop.validation;

import javax.validation.GroupSequence;

@GroupSequence({ValidationGroups.NotBlankGroup.class, ValidationGroups.SizeCheckGroup.class,
                ValidationGroups.PatternCheckGroup.class})
public interface ValidationSequence {
}
