package unam.ciencias.modeladoyprogramacion.capitalgains.config;

import java.io.PrintStream;
import java.util.Scanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StdInAndStdOutConfig {

  @Bean
  public Scanner getStdIn() {
    return new Scanner(System.in);
  }

  @Bean
  public PrintStream getStdOut() {
    return System.out;
  }
}
