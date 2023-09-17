package com.andrijatomic.contactmanager.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import java.text.DecimalFormat;
import java.util.Random;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

  @Value("${twilio.account}")
  private static String TWILIO_ACCOUNT_SID;
  @Value("${twilio.authtoken}")
  private static String TWILIO_AUTH_TOKEN;

  public String sendSms(String userPhoneNumber) {

    Twilio.init(TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN);

    String verificationCode = new DecimalFormat("000000")
        .format(new Random().nextInt(999999));

    Message.creator(new PhoneNumber(userPhoneNumber), new PhoneNumber("+15417128092"),
        "Your verification code is: " + verificationCode
    ).create();

    return verificationCode;

  }
}
