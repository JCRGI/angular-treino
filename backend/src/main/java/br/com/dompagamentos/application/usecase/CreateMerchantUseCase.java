package br.com.dompagamentos.application.usecase;

import br.com.dompagamentos.application.ports.input.CreateMerchantInputPort;
import br.com.dompagamentos.application.ports.output.MerchantRepositoryOutputPort;
import br.com.dompagamentos.application.ports.output.PaymentGatewayOutputPort;
import br.com.dompagamentos.domain.exception.DomainException;
import br.com.dompagamentos.domain.model.Merchant;
import br.com.dompagamentos.domain.model.enums.PspProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Use Case — cadastra um novo merchant na plataforma.
 *
 * Fluxo:
 *  1. Valida unicidade do documento
 *  2. Cria entidade Merchant no domínio
 *  3. Cria subconta no Asaas (padrão inicial)
 *  4. Persiste merchant com ID externo do PSP
 */
@Service
@Transactional
public class CreateMerchantUseCase implements CreateMerchantInputPort {

    private static final Logger log = LoggerFactory.getLogger(CreateMerchantUseCase.class);

    private final MerchantRepositoryOutputPort merchantRepository;
    private final Map<PspProvider, PaymentGatewayOutputPort> gateways;

    public CreateMerchantUseCase(
            MerchantRepositoryOutputPort merchantRepository,
            Map<PspProvider, PaymentGatewayOutputPort> gateways) {
        this.merchantRepository = merchantRepository;
        this.gateways = gateways;
    }

    @Override
    public Merchant execute(Command command) {
        if (merchantRepository.existsByDocument(command.document())) {
            throw new DomainException("Já existe um merchant cadastrado com este documento: " + command.document());
        }

        Merchant merchant = Merchant.create(
                command.name(),
                command.document(),
                command.email(),
                command.phone()
        );

        if (command.callbackUrl() != null && !command.callbackUrl().isBlank()) {
            merchant.registerCallbackUrl(command.callbackUrl());
        }

        // Cria subconta no Asaas (PSP padrão para novos merchants)
        try {
            String externalId = gateways.get(PspProvider.ASAAS).createSubAccount(merchant);
            merchant.assignPsp(PspProvider.ASAAS, externalId);
            log.info("Subconta Asaas criada para merchant {}: {}", merchant.getId(), externalId);
        } catch (Exception e) {
            log.warn("Falha ao criar subconta no Asaas para merchant {}. Continuando sem externalId.", merchant.getId(), e);
        }

        return merchantRepository.save(merchant);
    }
}
