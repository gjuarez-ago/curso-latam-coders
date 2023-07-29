package com.services.curso.utils;

import static com.services.curso.constant.EmailConstant.*;
import static javax.mail.Message.RecipientType.TO;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

import com.sun.mail.smtp.SMTPTransport;

@Service
public class EmailService {

	  public void resetPassword(String firstName, String password, String email) throws MessagingException {
	        Message message = createEmail(firstName, password, email);
	        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
	        smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
	        smtpTransport.sendMessage(message, message.getAllRecipients());
	        smtpTransport.close();
	    }

	    private Message createEmail(String firstName, String token, String email) throws MessagingException {
	        Message message = new MimeMessage(getEmailSession());
	        message.setFrom(new InternetAddress(FROM_EMAIL));
	        message.setRecipients(TO, InternetAddress.parse(email, false));
//	        message.setRecipients(CC, InternetAddress.parse(CC_EMAIL, false));
	        message.setSubject(EMAIL_SUBJECT);
	        message.setContent("<!DOCTYPE html>\r\n"
	        		+ "<html lang=\"en\">\r\n"
	        		+ "<head>\r\n"
	        		+ "    <meta charset=\"UTF-8\">\r\n"
	        		+ "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\r\n"
	        		+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n"
	        		+ "    <title>Recuperar cuenta</title>\r\n"
	        		+ "</head>\r\n"
	        		+ "<body>\r\n"
	        		+ "    <div style=\"background-color: rgba(255, 255, 255, 0.2); padding: 50px; text-align: center; margin: 0 auto; font-family: sans-serif;\">\r\n"
	        		+ "        <img src=\"https://www.agoconsultores.mx/planogram-frontend/assets/img/logo.png\" style=\"width: 180px;\" alt=\"\" />\r\n"
	        		+ "        <div><img src=\"https://www.agoconsultores.mx/planogram-frontend/assets/img/change-password.png\" style=\"width: 270px; margin: 60px 0px 20px 0px;\" alt=\"Imagen-contraseña\" /></div>\r\n"
	        		+ "        <div><h1>&iquest;Quiéres cambiar la contrase&ntilde;a?</h1></div>\r\n"
	        		+ "        <div style=\"width: 50%; box-shadow: rgba(0, 0, 0, 0.25) 0px 54px 55px, rgba(0, 0, 0, 0.12) 0px -12px 30px, rgba(0, 0, 0, 0.12) 0px 4px 6px, rgba(0, 0, 0, 0.17) 0px 12px 13px, rgba(0, 0, 0, 0.09) 0px -3px 5px; padding: 1px; margin: 10px auto 30px auto;\"></div>\r\n"
	        		+ "        <div class=\"more-text\">\r\n"
	        		+ "            <h2 style=\"margin: 0px; color: rgb(62, 62, 62); font-weight: 600;\"> &iquest;Ha pedido cambiar la contrase&ntilde;a?</h2>\r\n"
	        		+ "            <h3 style=\"margin: 0px 0px 30px 0px; color: rgb(65, 65, 65); font-weight: 300;\"> Presione el botón para cambiar la contrase&ntilde;a?</h3>\r\n"
	        		+ "        </div>\r\n"
	        		+ "        <div style=\"display: flex; justify-content: center; margin: 10px 0px;\">\r\n"
	        		+ "        <a href=\"https://www.agoconsultores.mx/planogram-frontend/#/auth/reset-password/" + token + "/" + email +  "\">"
	        		+ "            <button class=\"button-22\" role=\"button\"  style=\"align-items: center; appearance: button; background-color: #0276FF; border-radius: 8px; border-style: none; box-shadow: rgba(255, 255, 255, 0.26) 0 1px 2px inset; box-sizing: border-box; color: #fff; cursor: pointer; display: flex; flex-direction: row; flex-shrink: 0; font-size: 100%;line-height: 1.15; margin: 0; padding: 10px 21px; text-align: center; text-transform: none; transition: color .13s ease-in-out,background .13s ease-in-out,opacity .13s ease-in-out,box-shadow .13s ease-in-out;\">Cambiar contraseña</button>\r\n"
	        		+ "        </a>\n"
	        		+ "        </div>\r\n"
	        		+ "        <div style=\"color: rgb(119, 119, 119); font-weight: 500;\"><h3> Si no pidio el cambio de contrase&ntilde;a por favor ignore el correo</h3></div>\r\n"
	        		+ "        <div style=\"color: rgb(119, 119, 119); font-weight: 500;\"><p style=\"margin: 0px;\"> AGO Consultores @2022 </p><p style=\"margin: 0px;\"> Todos los derechos reservados </p></div>\r\n"
	        		+ "    </div>\r\n"
	        		+ "</body>\r\n"
	        		+ "</html>", "text/html");
	        
	        
	        //        message.setText("Hola " + firstName + ", \n \n Restablece tu contraseña en la siguiente url:  http://localhost:4200/auth/reset-password/" + token  +   "/"  + email +  "\n \n Nova Support");
	        message.setSentDate(new Date());
	        message.saveChanges();
	        return message;
	    }
	    
	    public void sendNewPasswordEmail(String firstName, String password, String email) throws MessagingException {
	        Message message = createMessage(firstName, password, email);
	        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
	        smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
	        smtpTransport.sendMessage(message, message.getAllRecipients());
	        smtpTransport.close();
	    }
	    
	    private Message createMessage(String firstName, String token, String email) throws MessagingException {
	        Message message = new MimeMessage(getEmailSession());
	        message.setFrom(new InternetAddress(FROM_EMAIL));
	        message.setRecipients(TO, InternetAddress.parse(email, false));
//	        message.setRecipients(CC, InternetAddress.parse(CC_EMAIL, false));
	        message.setSubject("Servicios AGO Consultores | Activar cuenta");
	        message.setContent("<!DOCTYPE html>\r\n"
	        		+ "<html lang=\"en\">\r\n"
	        		+ "<head>\r\n"
	        		+ "    <meta charset=\"UTF-8\">\r\n"
	        		+ "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\r\n"
	        		+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n"
	        		+ "    <title>Activar cuenta</title>\r\n"
	        		+ "</head>\r\n"
	        		+ "<body>\r\n"
	        		+ "    <div style=\"background-color: rgba(255, 255, 255, 0.2); padding: 50px; text-align: center; margin: 0 auto;font-family: sans-serif; \">\r\n"
	        		+ "        <img src=\"https://www.agoconsultores.mx/planogram-frontend/assets/img/logo.svg\" style=\"width: 180px;\" alt=\"\" />       \r\n"
	        		+ "        <div style=\"padding: 40px; background-color: #0276FF; width: 50%; margin: 40px auto 20px auto; border-radius: 20px;\">\r\n"
	        		+ "            <h1 style=\"color: rgb(255, 255, 255); font-weight: 600; margin: 0px 0px;\" >Confirma tu correo</h1>\r\n"
	        		+ "            <img src=\"https://www.agoconsultores.mx/planogram-frontend/assets/img/recovery-password.png\" style=\"margin: 20px 0px 10px 0px;\" alt=\"\" />\r\n"
	        		+ "        </div>\r\n"
	        		+ "        <div> <h2>Gracias por registrarte y crear una cuenta con nosotros</h2></div>\r\n"
	        		+ "        <div class=\"size-box\"></div>\r\n"
	        		+ "        <div style=\"width: 80%; text-align: center; margin: 20px auto;\" > <p> Para comenzar a disfrutar, solo tienes que confirmar tu dirección de correo en el botón para que podamos comprobar correctamente tu cuenta.</p></div>\r\n"
	        		+ "        <div style=\"display: flex; justify-content: center;\">\r\n"
	        		+ "        <a href=\"https://www.agoconsultores.mx/planogram-frontend/#/auth/reset-password/" + token + "/" + email +  "\">"
	        		+ "            <button class=\"button-22\" role=\"button\"  style=\"align-items: center; appearance: button; background-color: #0276FF; border-radius: 8px; border-style: none; box-shadow: rgba(255, 255, 255, 0.26) 0 1px 2px inset; box-sizing: border-box; color: #fff; cursor: pointer; display: flex; flex-direction: row; flex-shrink: 0; font-size: 100%;line-height: 1.15; margin: 10px 0; padding: 10px 21px; text-align: center; text-transform: none; transition: color .13s ease-in-out,background .13s ease-in-out,opacity .13s ease-in-out,box-shadow .13s ease-in-out; user-select: none;-webkit-user-select: none; touch-action: manipulation;\">Confirmar correo</button>\r\n"
	        		+ "        </a>\n"
	        		+ "        </div>\r\n"
	        		+ "        <div style=\"color: rgb(119, 119, 119); font-weight: 500; margin: 20px 0px;\"><p style=\"margin: 0px;\"> AGO Consultores @2022 </p> <p style=\"margin: 0px;\"> Todos los derechos reservados </p></div>\r\n"
	        		+ "    </div>\r\n"
	        		+ "</body>\r\n"
	        		+ "</html>", "text/html");
	        //        message.setText("Hola " + firstName + ", \n \n El password para tu cuenta es: " + token + "\n \n Ingresa a nuestro sitio: https://portal.macropay.mx/login" + "\n \n Team SAP");
	        message.setSentDate(new Date());
	        message.saveChanges();
	        return message;
	    }
	 
	    public void sendNotification(String firstName, String orderId, String email) throws MessagingException {
	        Message message = createMessageN(firstName, orderId, email);
	        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
	        smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
	        smtpTransport.sendMessage(message, message.getAllRecipients());
	        smtpTransport.close();
	    }
	    
	    private Message createMessageN(String firstName, String orderId, String email) throws MessagingException {
	        Message message = new MimeMessage(getEmailSession());
	        message.setFrom(new InternetAddress(FROM_EMAIL));
	        message.setRecipients(TO, InternetAddress.parse(email, false));
//	        message.setRecipients(CC, InternetAddress.parse(CC_EMAIL, false));
	        message.setSubject("Cotta | Orden No." + orderId);
	        message.setContent("<!DOCTYPE html>\n"
	        		+ "<html lang=\"en\">\n"
	        		+ "<head>\n"
	        		+ "    <meta charset=\"UTF-8\">\n"
	        		+ "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n"
	        		+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
	        		+ "    <title>Password</title>\n"
	        		+ "</head>\n"
	        		+ "<body>\n"
	        		+ "    \n"
	        		+ "        <center class=\"wrapper\">\n"
	        		+ "            <table class=\"top-panel center\" width=\"602\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n"
	        		+ "                <tbody>\n"
	        		+ "                <tr>\n"
	        		+ "                    <td class=\"title\" width=\"300\">Ago Consultores</td>\n"
	        		+ "                    <td class=\"subject\" width=\"300\"><a class=\"strong\" href=\"#\" target=\"_blank\">www.cotta-store.com</a></td>\n"
	        		+ "                </tr>\n"
	        		+ "                <tr>\n"
	        		+ "                    <td class=\"border\" colspan=\"2\">&nbsp;</td>\n"
	        		+ "                </tr>\n"
	        		+ "                </tbody>\n"
	        		+ "            </table>\n"
	        		+ "        \n"
	        		+ "            <div class=\"spacer\">&nbsp;</div>\n"
	        		+ "        \n"
	        		+ "            <table class=\"main center\" width=\"602\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n"
	        		+ "                <tbody>\n"
	        		+ "                <tr>\n"
	        		+ "                    <td class=\"column\">\n"
	        		+ "                        <div class=\"column-top\">&nbsp;</div>\n"
	        		+ "                        <table class=\"content\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n"
	        		+ "                            <tbody>\n"
	        		+ "                            <tr>\n"
	        		+ "                                <td class=\"padded\">\n"
	        		+ "                                  <h1> Orden creada correctamente </h1>\n"
	        		+ "                                  <p> Tu orden se ha <strong> generado correctamente </strong> sigue las siguientes indicaciones:</p>\n"
	        		+ "                                  <p>Presiona el link con la leyenda 'Ver mi orden', para saber el estatus y detalles de tu orden: </p>\n"
	        		+ "                                  <p style=\"text-align:center;\">"
	        		+ "                                  <a href=\"http://localhost:4200/order/" + orderId + "\""
	        		+ "                                   class=\"btn\">"
	        		+ "                                   Ver mi orden </a>"
	        		+ "                                  </p>\n"
	        		+ "                                  <p style=\"text-align:center;\">\n"
	        		+ "                                    <a class=\"strong\">En cotta estamos comprometidos con su bienestar. </a>\n"
	        		+ "                                  </p>\n"
	        		+ "                                  <p style=\"text-align:center;\"> Gracias por su atención :). </p>\n"
	        		+ "                                </td>\n"
	        		+ "                            </tr>\n"
	        		+ "                            </tbody>\n"
	        		+ "                        </table>\n"
	        		+ "                        <div class=\"column-bottom\">&nbsp;</div>\n"
	        		+ "                    </td>\n"
	        		+ "                </tr>\n"
	        		+ "                </tbody>\n"
	        		+ "            </table>\n"
	        		+ "        \n"
	        		+ "            <div class=\"spacer\">&nbsp;</div>\n"
	        		+ "        \n"
	        		+ "            <table class=\"footer center\" width=\"602\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n"
	        		+ "                <tbody>\n"
	        		+ "                <tr>\n"
	        		+ "                    <td class=\"border\" colspan=\"2\">&nbsp;</td>\n"
	        		+ "                </tr>\n"
	        		+ "                <tr>\n"
	        		+ "                    <td class=\"signature\" width=\"300\">\n"
	        		+ "                        <p>\n"
	        		+ "                            Soporte Cotta AS,<br>\n"
	        		+ "                            Cotta Inc.<br>\n"
	        		+ "                            +0 (000) 00-00-00, John Doe<br>\n"
	        		+ "                            </p>\n"
	        		+ "                        <p>\n"
	        		+ "                            Support: <a class=\"strong\" href=\"mailto:#\" target=\"_blank\">support@cotta.mx</a>\n"
	        		+ "                        </p>\n"
	        		+ "                    </td>\n"
	        		+ "                    <td class=\"subscription\" width=\"300\">\n"
	        		+ "                        <div class=\"logo-image\">\n"
	        		+ "                            <a href=\"https://www.agoconsultores.com.mx\" target=\"_blank\"><img src=\"https://www.agoconsultores.com.mx/wp-content/uploads/2022/04/AgoConsultores170x80.png\" alt=\"logo-alt\" width=\"170\" height=\"80\"></a>\n"
	        		+ "                        </div>\n"
	        		+ "                        <p>\n"
	        		+ "                            <a class=\"strong block\" href=\"http://localhost:4200/#/auth/login\" target=\"_blank\">\n"
	        		+ "                                Iniciar sesión\n"
	        		+ "                            </a>\n"
	        		+ "                            <span class=\"hide\">&nbsp;&nbsp;|&nbsp;&nbsp;</span>\n"
	        		+ "                            <a class=\"strong block\" href=\"#\" target=\"_blank\">\n"
	        		+ "                                Aviso de privacidad\n"
	        		+ "                            </a>\n"
	        		+ "                        </p>\n"
	        		+ "                    </td>\n"
	        		+ "                </tr>\n"
	        		+ "                </tbody>\n"
	        		+ "            </table>\n"
	        		+ "        </center>\n"
	        		+ "\n"
	        		+ "\n"
	        		+ "</body>\n"
	        		+ "</html>", "text/html");
	        
	        
	        message.setSentDate(new Date());
	        message.saveChanges();
	        return message;
	    }

	   private Session getEmailSession() {
	        Properties properties = System.getProperties();
	        properties.put(SMTP_HOST, GMAIL_SMTP_SERVER);
	        properties.put(SMTP_AUTH, true);
	        properties.put(SMTP_PORT, DEFAULT_PORT);
	        properties.put(SMTP_STARTTLS_ENABLE, true);
	        properties.put(SMTP_STARTTLS_REQUIRED, true);
	        return Session.getInstance(properties, null);
	   }

}
