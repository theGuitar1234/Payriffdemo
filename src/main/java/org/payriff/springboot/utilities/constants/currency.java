package org.payriff.springboot.utilities.constants;

public enum currency {
    USD("$"), 
    EURO("£"), 
    TRY("₺"), 
    AZN("₼");

    private String currencySign;

    private currency(String currencySign) {
        this.currencySign = currencySign;
    }

    public String getCurrencySign() {
        return this.currencySign;
    }
}
