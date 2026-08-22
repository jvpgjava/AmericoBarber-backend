package com.americobarber.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PhoneNumberUtilTest {

    @Test
    void addsCountryCodeToPlainLocalNumber() {
        assertEquals("+5511999998888", PhoneNumberUtil.toE164Brazil("11999998888"));
    }

    @Test
    void stripsPunctuationBeforeNormalizing() {
        assertEquals("+5511999998888", PhoneNumberUtil.toE164Brazil("(11) 99999-8888"));
    }

    @Test
    void keepsAlreadyE164NumberUnchanged() {
        assertEquals("+5511999998888", PhoneNumberUtil.toE164Brazil("+5511999998888"));
    }

    @Test
    void doesNotDoubleCountryCodeForDddStartingWith55() {
        assertEquals("+5555991234567", PhoneNumberUtil.toE164Brazil("55991234567"));
    }

    @Test
    void returnsNullForBlankOrNullInput() {
        assertNull(PhoneNumberUtil.toE164Brazil(""));
        assertNull(PhoneNumberUtil.toE164Brazil("   "));
        assertNull(PhoneNumberUtil.toE164Brazil(null));
    }
}
