package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountTest {

    @Mock
    private DateProvider dateProvider;
    @Mock
    private StatementPrinter printer;
    @Captor
    ArgumentCaptor<String> lineCaptor;

    @Test
    void should_print_statement_containing_all_transactions() {
        AccountService account = new Account(dateProvider, printer);

        // Given
        LocalDate date1 = LocalDate.of(2012, 1, 10);
        LocalDate date2 = LocalDate.of(2012, 1, 13);
        LocalDate date3 = LocalDate.of(2012, 1, 14);

        given(dateProvider.today()).willReturn(date1, date2, date3);

        account.deposit(1000);  // 10/01
        account.deposit(2000);  // 13/01
        account.withdraw(500);  // 14/01

        // When
        account.printStatement();

        // THEN
        verify(printer, times(1)).print(lineCaptor.capture());

        String fullStatement = lineCaptor.getValue();

        assertThat(fullStatement).isEqualTo(
                "Date || Amount || Balance\n" +
                        "14/01/2012 || -500 || 2500\n" +
                        "13/01/2012 || 2000 || 3000\n" +
                        "10/01/2012 || 1000 || 1000"
        );
    }

    @Test
    void should_print_only_header_when_no_transactions() {
        // Given
        AccountService account = new Account(dateProvider, printer);

        // When
        account.printStatement();

        // Then
        verify(printer, times(1)).print(lineCaptor.capture());

        String printedLine = lineCaptor.getValue();

        assertThat(printedLine).isEqualTo("Date || Amount || Balance");
    }

    @Test
    void should_throw_exception_when_depositing_negative_amount() {
        // GIVEN
        AccountService account = new Account(dateProvider, printer);

        // WHEN & THEN
        assertThatThrownBy(() -> account.deposit(-100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Le montant du depot doit etre positif");
    }

    @Test
    void should_throw_exception_when_depositing_zero() {
        // Given
        AccountService account = new Account(dateProvider, printer);

        // When & Then
        assertThatThrownBy(() -> account.deposit(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Le montant du depot doit etre positif");
    }


    @Test
    void should_throw_exception_when_withdrawing_negative_amount() {
        // GIVEN
        AccountService account = new Account(dateProvider, printer);

        // WHEN & THEN
        assertThatThrownBy(() -> account.withdraw(-500))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Le montant du retrait doit etre positif");
    }

    @Test
    void should_throw_exception_when_withdrawing_zero() {
        // Given
        AccountService account = new Account(dateProvider, printer);

        // When & Then
        assertThatThrownBy(() -> account.withdraw(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Le montant du retrait doit etre positif");
    }

    @Test
    void should_throw_exception_when_withdrawing_more_than_balance() {
        AccountService account = new Account(dateProvider, printer);
        // Given
        given(dateProvider.today()).willReturn(LocalDate.of(2023, 1, 1));
        account.deposit(100);
        // WHEN & THEN
        assertThatThrownBy(() -> account.withdraw(200))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Solde insuffisant");
    }

    @Test
    void should_throw_exception_when_deposit_causes_overflow() {
        AccountService account = new Account(dateProvider, printer);
        // Given
        given(dateProvider.today()).willReturn(LocalDate.of(2023, 1, 1));
        // WHEN
        account.deposit(Integer.MAX_VALUE);
        // Then
        assertThatThrownBy(() -> account.deposit(1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Le solde depasse la capacité maximale autorisee");
    }
}
