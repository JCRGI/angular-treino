package br.com.dompagamentos.domain.service;

import br.com.dompagamentos.domain.model.Merchant;
import br.com.dompagamentos.domain.model.enums.PspProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PspRoutingServiceTest {

    private PspRoutingService service;

    // Threshold: R$ 500.000 = 50.000.000 centavos
    private static final BigDecimal THRESHOLD = new BigDecimal("50000000");

    @BeforeEach
    void setUp() {
        service = new PspRoutingService(THRESHOLD);
    }

    @Test
    @DisplayName("Merchant com volume abaixo do threshold deve ser roteado para ASAAS")
    void shouldRouteToAsaasWhenVolumeBelowThreshold() {
        Merchant merchant = Merchant.create("Loja Pequena", "12345678000195", "loja@email.com", null);
        merchant.updateMonthlyVolume(new BigDecimal("1000000")); // R$ 10.000

        PspProvider result = service.route(merchant);

        assertThat(result).isEqualTo(PspProvider.ASAAS);
    }

    @Test
    @DisplayName("Merchant com volume exato no threshold deve ir para IUGU")
    void shouldRouteToIuguWhenVolumeEqualsThreshold() {
        Merchant merchant = Merchant.create("Loja Grande", "12345678000195", "loja@email.com", null);
        merchant.updateMonthlyVolume(THRESHOLD);

        assertThat(service.isEligibleForIugu(merchant)).isTrue();
    }

    @Test
    @DisplayName("Merchant com PSP preferido manual deve respeitar a preferência")
    void shouldRespectMerchantPreference() {
        Merchant merchant = Merchant.create("Loja Especial", "12345678000195", "loja@email.com", null);
        merchant.assignPsp(PspProvider.IUGU, "iugu-ext-id-123");

        PspProvider result = service.route(merchant);

        assertThat(result).isEqualTo(PspProvider.IUGU);
    }
}
