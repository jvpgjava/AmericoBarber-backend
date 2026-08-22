package com.americobarber.util;

public final class PhoneNumberUtil {

    private static final String BRAZIL_COUNTRY_CODE = "55";

    private PhoneNumberUtil() {
    }

    /**
     * Normaliza um número de telefone brasileiro em formato livre (ex: "(11) 99999-8888")
     * para o formato E.164 exigido para envio de WhatsApp (ex: "+5511999998888").
     */
    public static String toE164Brazil(String rawPhone) {
        if (rawPhone == null || rawPhone.isBlank()) {
            return null;
        }
        String digits = rawPhone.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            return null;
        }
        // Números locais (DDD + telefone) têm 10 ou 11 dígitos; a partir daí, já inclui o código do país.
        // Checar o tamanho evita confundir DDDs que começam com "55" (ex: Santa Maria/RS) com o código do Brasil.
        if (digits.length() <= 11) {
            digits = BRAZIL_COUNTRY_CODE + digits;
        }
        return "+" + digits;
    }
}
