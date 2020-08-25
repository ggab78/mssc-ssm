package com.gabriel.msscssm.services;

import com.gabriel.msscssm.domain.Payment;
import com.gabriel.msscssm.domain.PaymentEvent;
import com.gabriel.msscssm.domain.PaymentState;
import com.gabriel.msscssm.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;


@RequiredArgsConstructor
@Component
public class PaymentStateChangeInterceptor extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {

    private final PaymentRepository paymentRepository;


    @Override
    public void preStateChange(State<PaymentState, PaymentEvent> state,
                               Message<PaymentEvent> message, Transition<PaymentState, PaymentEvent> transition,
                               StateMachine<PaymentState, PaymentEvent> stateMachine) {

        System.out.println("TRANSITION :"+transition.getTarget());
        System.out.println("STATE :"+state.getId());
        System.out.println("STATE MACHINE :"+stateMachine.getState());

        Optional.ofNullable(message).ifPresent(msg->{
            Optional.ofNullable(Long.class.cast(msg.getHeaders().getOrDefault(PaymentServiceImpl.PAYMENT_ID_HEADER,-1L)))
                    .ifPresent(id->{
                        Payment payment = paymentRepository.getOne(id);
                        payment.setState(state.getId());
                        paymentRepository.save(payment);
                    });
        });
    }
}
