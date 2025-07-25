package com.carelink.notification.emailService;

public class EmailTemplateBuilder {

    public static String buildPasswordResetEmailHtml(String firstName, String resetLink) {
        String title = "Password Reset Request";
        String bodyContent = String.format("""
    <p>Hi %s,</p>
    <p>We received a request to reset your password. Click the button below:</p>
    <p style="text-align: center;">
        <a href="%s" style="display: inline-block; background-color: #2a7ec5; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px;">Reset Password</a>
    </p>
    <p>If the button doesn't work, use the link below:</p>
    <p><a href="%s">%s</a></p>
""", firstName, resetLink, resetLink, resetLink);
        return String.format("""
    <html>
      <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <style>
          @media (prefers-color-scheme: dark) {
            body {
              background-color: #1e1e1e !important;
              color: #f0f0f0 !important;
            }
            table {
              background-color: #2e2e2e !important;
            }
            a {
              color: #66bfff !important;
            }
          }
        </style>
      </head>
      <body style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4; padding: 40px;">
        <table style="max-width: 600px; margin: auto; background-color: #ffffff; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
          <tr>
            <td style="text-align: center;">
              <img src="https://raw.githubusercontent.com/ViniChevalier/CareLinkPlus/c67ead495b6db65d690a98ce699597cbba49b83e/Frontend/assets/img/CareLink_Logo.png" alt="CareLink+ Logo" style="max-width: 150px; height: auto; margin-bottom: 20px;" />
              <h2 style="color: #2a7ec5;">%s</h2>
            </td>
          </tr>
          <tr>
            <td style="font-size: 15px; color: #333333;">
              %s
            </td>
          </tr>
        </table>
      </body>
    </html>
    """, title, bodyContent);
    }

    public static String buildPasswordResetEmailText(String firstName, String resetLink) {
        return String.format("""
                Hello %s,

                We received a request to reset your password.

                Reset your password using this link: %s

                If you did not request this, please ignore this message.
                """, firstName, resetLink);
    }

    public static String buildWelcomeEmailText(String firstName, String username, String password) {
        String title = "Welcome to CareLink+";
        String bodyContent = String.format("""
        <p>Hi %s,</p>
        <p>Welcome to <strong>CareLink+</strong>! Your account has been created successfully.</p>
        <p><strong>Username:</strong> %s<br/>
        <strong>Temporary Password:</strong> <code style="background-color: #eee; padding: 4px 8px; border-radius: 4px;">%s</code></p>
        <p style="text-align: center; margin-top: 20px;">
            <a href="https://calm-sky-0157a6e03.1.azurestaticapps.net/login"
               style="background-color: #2a7ec5; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px;">
               Access Your Account
            </a>
        </p>
        <p>Please change your password after logging in to ensure your security.</p>
    """, firstName, username, password);

        return String.format("""
        <html>
          <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            <style>
              @media (prefers-color-scheme: dark) {
                body {
                  background-color: #1e1e1e !important;
                  color: #f0f0f0 !important;
                }
                table {
                  background-color: #2e2e2e !important;
                }
                a {
                  color: #66bfff !important;
                }
              }
            </style>
          </head>
          <body style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4; padding: 40px;">
            <table style="max-width: 600px; margin: auto; background-color: #ffffff; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
              <tr>
                <td style="text-align: center;">
                  <img src="https://raw.githubusercontent.com/ViniChevalier/CareLinkPlus/c67ead495b6db65d690a98ce699597cbba49b83e/Frontend/assets/img/CareLink_Logo.png" alt="CareLink+ Logo" style="max-width: 150px; height: auto; margin-bottom: 20px;" />
                  <h2 style="color: #2a7ec5;">%s</h2>
                </td>
              </tr>
              <tr>
                <td style="font-size: 15px; color: #333333;">
                  %s
                </td>
              </tr>
            </table>
          </body>
        </html>
    """, title, bodyContent);
    }

    public static String buildAdminResetEmailHtml(String firstName, String password) {
        String title = "Password Reset Notification";
        String bodyContent = String.format("""
    <p>Hi %s,</p>
    <p>Your password has been reset by an administrator.</p>
    <p><strong>Temporary Password:</strong> <code style="background-color: #eee; padding: 4px 8px; border-radius: 4px;">%s</code></p>
    <p style="text-align: center; margin-top: 20px;">
        <a href="https://calm-sky-0157a6e03.1.azurestaticapps.net/login"
           style="background-color: #2a7ec5; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px;">
           Log In to Your Account
        </a>
    </p>
""", firstName, password);
        return String.format("""
    <html>
      <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <style>
          @media (prefers-color-scheme: dark) {
            body {
              background-color: #1e1e1e !important;
              color: #f0f0f0 !important;
            }
            table {
              background-color: #2e2e2e !important;
            }
            a {
              color: #66bfff !important;
            }
          }
        </style>
      </head>
      <body style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4; padding: 40px;">
        <table style="max-width: 600px; margin: auto; background-color: #ffffff; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
          <tr>
            <td style="text-align: center;">
              <img src="https://raw.githubusercontent.com/ViniChevalier/CareLinkPlus/c67ead495b6db65d690a98ce699597cbba49b83e/Frontend/assets/img/CareLink_Logo.png" alt="CareLink+ Logo" style="max-width: 150px; height: auto; margin-bottom: 20px;" />
              <h2 style="color: #2a7ec5;">%s</h2>
            </td>
          </tr>
          <tr>
            <td style="font-size: 15px; color: #333333;">
              %s
            </td>
          </tr>
        </table>
      </body>
    </html>
    """, title, bodyContent);
    }

    public static String buildAdminResetEmailText(String firstName, String password) {
        return String.format("""
            Hello %s,

            Your password has been reset by a CareLink+ administrator.

            Temporary Password: %s

            Log in at: https://calm-sky-0157a6e03.1.azurestaticapps.net/login
            """, firstName, password);
    }

    public static String buildAppointmentNotificationEmail(String firstName, String doctorName, String date, String time) {
        String title = "New Appointment Scheduled";
        String bodyContent = String.format("""
        <p>Hi %s,</p>
        <p>You have successfully scheduled an appointment with <strong>Dr. %s</strong>.</p>
        <p><strong>Date:</strong> %s<br/>
        <strong>Time:</strong> %s</p>
        <p style="text-align: center; margin-top: 20px;">
            <a href="https://calm-sky-0157a6e03.1.azurestaticapps.net/view-appointments.html"
               style="background-color: #2a7ec5; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px;">
               View Appointment
            </a>
        </p>
        """, firstName, doctorName, date, time);

        return String.format("""
        <html>
          <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            <style>
              @media (prefers-color-scheme: dark) {
                body {
                  background-color: #1e1e1e !important;
                  color: #f0f0f0 !important;
                }
                table {
                  background-color: #2e2e2e !important;
                }
                a {
                  color: #66bfff !important;
                }
              }
            </style>
          </head>
          <body style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4; padding: 40px;">
            <table style="max-width: 600px; margin: auto; background-color: #ffffff; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
              <tr>
                <td style="text-align: center;">
                  <img src="https://raw.githubusercontent.com/ViniChevalier/CareLinkPlus/c67ead495b6db65d690a98ce699597cbba49b83e/Frontend/assets/img/CareLink_Logo.png" alt="CareLink+ Logo" style="max-width: 150px; height: auto; margin-bottom: 20px;" />
                  <h2 style="color: #2a7ec5;">%s</h2>
                </td>
              </tr>
              <tr>
                <td style="font-size: 15px; color: #333333;">
                  %s
                </td>
              </tr>
            </table>
          </body>
        </html>
        """, title, bodyContent);
    }

    public static String buildAppointmentReminderEmail(String firstName, String doctorName, String date, String time) {
        String title = "Appointment Reminder";
        String bodyContent = String.format("""
        <p>Hi %s,</p>
        <p>This is a reminder for your upcoming appointment with <strong>Dr. %s</strong>.</p>
        <p><strong>Date:</strong> %s<br/>
        <strong>Time:</strong> %s</p>
        <p>Please make sure to arrive 10 minutes early and bring any relevant documents.</p>
        """, firstName, doctorName, date, time);

        return String.format("""
        <html>
          <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            <style>
              @media (prefers-color-scheme: dark) {
                body {
                  background-color: #1e1e1e !important;
                  color: #f0f0f0 !important;
                }
                table {
                  background-color: #2e2e2e !important;
                }
                a {
                  color: #66bfff !important;
                }
              }
            </style>
          </head>
          <body style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4; padding: 40px;">
            <table style="max-width: 600px; margin: auto; background-color: #ffffff; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
              <tr>
                <td style="text-align: center;">
                  <img src="https://raw.githubusercontent.com/ViniChevalier/CareLinkPlus/c67ead495b6db65d690a98ce699597cbba49b83e/Frontend/assets/img/CareLink_Logo.png" alt="CareLink+ Logo" style="max-width: 150px; height: auto; margin-bottom: 20px;" />
                  <h2 style="color: #2a7ec5;">%s</h2>
                </td>
              </tr>
              <tr>
                <td style="font-size: 15px; color: #333333;">
                  %s
                </td>
              </tr>
            </table>
          </body>
        </html>
        """, title, bodyContent);
    }
    public static String buildAppointmentCancellationEmail(String firstName, String doctorName, String date, String time) {
        String title = "Appointment Cancelled";
        String bodyContent = String.format("""
        <p>Hi %s,</p>
        <p>Your appointment with <strong>Dr. %s</strong> on <strong>%s</strong> at <strong>%s</strong> has been cancelled.</p>
        <p>If this was a mistake or you wish to reschedule, please visit your account:</p>
        <p style="text-align: center; margin-top: 20px;">
            <a href="https://calm-sky-0157a6e03.1.azurestaticapps.net/view-appointments.html"
               style="background-color: #2a7ec5; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px;">
               Manage Appointments
            </a>
        </p>
        """, firstName, doctorName, date, time);

        return String.format("""
        <html>
          <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            <style>
              @media (prefers-color-scheme: dark) {
                body {
                  background-color: #1e1e1e !important;
                  color: #f0f0f0 !important;
                }
                table {
                  background-color: #2e2e2e !important;
                }
                a {
                  color: #66bfff !important;
                }
              }
            </style>
          </head>
          <body style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4; padding: 40px;">
            <table style="max-width: 600px; margin: auto; background-color: #ffffff; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
              <tr>
                <td style="text-align: center;">
                  <img src="https://raw.githubusercontent.com/ViniChevalier/CareLinkPlus/c67ead495b6db65d690a98ce699597cbba49b83e/Frontend/assets/img/CareLink_Logo.png" alt="CareLink+ Logo" style="max-width: 150px; height: auto; margin-bottom: 20px;" />
                  <h2 style="color: #2a7ec5;">%s</h2>
                </td>
              </tr>
              <tr>
                <td style="font-size: 15px; color: #333333;">
                  %s
                </td>
              </tr>
            </table>
          </body>
        </html>
        """, title, bodyContent);
    }
}