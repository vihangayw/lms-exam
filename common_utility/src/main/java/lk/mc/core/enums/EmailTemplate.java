package lk.mc.core.enums;

/**
 * EmailTemplate contains pre defined email templates, the enum name should match the file name of the templates
 * Theme leaf will search the templates from the following location.
 * HTML template location : resources/template/
 * ex: resources/template/welcome.html
 *
 * @author vihanga
 * @since 26/10/2021
 * MC-lms
 */
public enum EmailTemplate {

    qr, voucher, welcome, invoice, reminder1, reminder2, result_gau, result_othm,
    result_mc, exam, lms, initialpayment, email, email2, grad_approve, forget_pw
}
