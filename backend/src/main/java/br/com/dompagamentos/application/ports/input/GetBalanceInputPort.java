package br.com.dompagamentos.application.ports.input;

import java.math.BigDecimal;

public interface GetBalanceInputPort {

    BigDecimal execute();
}
