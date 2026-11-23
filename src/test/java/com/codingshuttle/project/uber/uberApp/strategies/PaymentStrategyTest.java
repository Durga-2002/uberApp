package com.codingshuttle.project.uber.uberApp.strategies;

import com.codingshuttle.project.uber.uberApp.entities.Driver;
import com.codingshuttle.project.uber.uberApp.entities.Payment;
import com.codingshuttle.project.uber.uberApp.entities.Ride;
import com.codingshuttle.project.uber.uberApp.entities.Rider;
import com.codingshuttle.project.uber.uberApp.entities.User;
import com.codingshuttle.project.uber.uberApp.entities.enums.PaymentMethod;
import com.codingshuttle.project.uber.uberApp.entities.enums.PaymentStatus;
import com.codingshuttle.project.uber.uberApp.entities.enums.TransactionMethod;
import com.codingshuttle.project.uber.uberApp.repositories.PaymentRepository;
import com.codingshuttle.project.uber.uberApp.services.WalletService;
import com.codingshuttle.project.uber.uberApp.strategies.impl.CashPaymentStrategy;
import com.codingshuttle.project.uber.uberApp.strategies.impl.WalletPaymentStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentStrategyTest {

    @Mock
    private WalletService walletService;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private WalletPaymentStrategy walletPaymentStrategy;
    @Mock
    private CashPaymentStrategy cashPaymentStrategy;

    private User riderUser;
    private User driverUser;
    private Ride ride;
    private Payment payment;

    @BeforeEach
    void setUp() {
        riderUser = new User();
        riderUser.setId(1L);
        driverUser = new User();
        driverUser.setId(2L);

        Rider rider = Rider.builder().id(1L).user(riderUser).build();
        Driver driver = Driver.builder().id(1L).user(driverUser).build();

        ride = new Ride();
        ride.setId(10L);
        ride.setRider(rider);
        ride.setDriver(driver);

        payment = Payment.builder()
                .amount(100.0)
                .ride(ride)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
    }

    @Test
    void managerSelectsWalletStrategy() {
        PaymentStrategyManager manager = new PaymentStrategyManager(walletPaymentStrategy, cashPaymentStrategy);

        assertSame(walletPaymentStrategy, manager.paymentStrategy(PaymentMethod.WALLET));
    }

    @Test
    void managerSelectsCashStrategy() {
        PaymentStrategyManager manager = new PaymentStrategyManager(walletPaymentStrategy, cashPaymentStrategy);

        assertSame(cashPaymentStrategy, manager.paymentStrategy(PaymentMethod.CASH));
    }

    @Test
    void cashPaymentDeductsOnlyCommissionFromDriverWallet() {
        new CashPaymentStrategy(walletService, paymentRepository).processPayment(payment);

        verify(walletService).deductMoneyFromWallet(driverUser, 30.0, null, ride, TransactionMethod.RIDE);
        assertEquals(PaymentStatus.CONFIRMED, payment.getPaymentStatus());
        verify(paymentRepository).save(payment);
    }

    @Test
    void walletPaymentDebitsRiderAndCreditsDriverNetOfCommission() {
        new WalletPaymentStrategy(walletService, paymentRepository).processPayment(payment);

        verify(walletService).deductMoneyFromWallet(riderUser, 100.0, null, ride, TransactionMethod.RIDE);
        verify(walletService).addMoneyToWallet(driverUser, 70.0, null, ride, TransactionMethod.RIDE);
        assertEquals(PaymentStatus.CONFIRMED, payment.getPaymentStatus());
        verify(paymentRepository).save(payment);
    }
}
