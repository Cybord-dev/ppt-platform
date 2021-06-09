package com.business.unknow.services.util.helpers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class NumberTranslatorHelper {

  private String cantidadConLetra(String s) {
    StringBuilder result = new StringBuilder();
    BigDecimal totalBigDecimal = new BigDecimal(s).setScale(2, RoundingMode.DOWN);
    long parteEntera = totalBigDecimal.toBigInteger().longValue();
    int triUnidades = (int) (parteEntera % 1000);
    int triMiles = (int) (parteEntera / 1000) % 1000;
    int triMillones = (int) (parteEntera / 1000000) % 1000;
    int triMilMillones = (int) (parteEntera / 1000000000) % 1000;

    if (parteEntera == 0) {
      result.append("Cero ");
      return result.toString();
    }

    if (triMilMillones > 0) result.append(triTexto(triMilMillones) + "Mil ");
    if (triMillones > 0) result.append(triTexto(triMillones));

    if (triMilMillones == 0 && triMillones == 1) result.append("Millón ");
    else if (triMilMillones > 0 || triMillones > 0) result.append("Millones ");

    if (triMiles > 0) result.append(triTexto(triMiles) + "Mil ");
    if (triUnidades > 0) result.append(triTexto(triUnidades));

    return result.toString();
  }

  /**
   * Convierte una cantidad de tres cifras a su representación escrita con letra.
   *
   * @param n La cantidad a convertir.
   * @return Una cadena de texto que contiene la representación con letra del número que se recibió
   *     como argumento.
   */
  private StringBuilder triTexto(int n) {
    StringBuilder result = new StringBuilder();
    int centenas = n / 100;
    int decenas = (n % 100) / 10;
    int unidades = (n % 10);

    switch (centenas) {
      case 0:
        break;
      case 1:
        if (decenas == 0 && unidades == 0) {
          result.append("Cien ");
          return result;
        } else result.append("Ciento ");
        break;
      case 2:
        result.append("Doscientos ");
        break;
      case 3:
        result.append("Trescientos ");
        break;
      case 4:
        result.append("Cuatrocientos ");
        break;
      case 5:
        result.append("Quinientos ");
        break;
      case 6:
        result.append("Seiscientos ");
        break;
      case 7:
        result.append("Setecientos ");
        break;
      case 8:
        result.append("Ochocientos ");
        break;
      case 9:
        result.append("Novecientos ");
        break;
    }

    switch (decenas) {
      case 0:
        break;
      case 1:
        if (unidades == 0) {
          result.append("Diez ");
          return result;
        } else if (unidades == 1) {
          result.append("Once ");
          return result;
        } else if (unidades == 2) {
          result.append("Doce ");
          return result;
        } else if (unidades == 3) {
          result.append("Trece ");
          return result;
        } else if (unidades == 4) {
          result.append("Catorce ");
          return result;
        } else if (unidades == 5) {
          result.append("Quince ");
          return result;
        } else result.append("Dieci");
        break;
      case 2:
        if (unidades == 0) {
          result.append("Veinte ");
          return result;
        } else result.append("Veinti");
        break;
      case 3:
        result.append("Treinta ");
        break;
      case 4:
        result.append("Cuarenta ");
        break;
      case 5:
        result.append("Cincuenta ");
        break;
      case 6:
        result.append("Sesenta ");
        break;
      case 7:
        result.append("Setenta ");
        break;
      case 8:
        result.append("Ochenta ");
        break;
      case 9:
        result.append("Noventa ");
        break;
    }

    if (decenas > 2 && unidades > 0) result.append("y ");

    switch (unidades) {
      case 0:
        break;
      case 1:
        result.append("Un ");
        break;
      case 2:
        result.append("Dos ");
        break;
      case 3:
        result.append("Tres ");
        break;
      case 4:
        result.append("Cuatro ");
        break;
      case 5:
        result.append("Cinco ");
        break;
      case 6:
        result.append("Seis ");
        break;
      case 7:
        result.append("Siete ");
        break;
      case 8:
        result.append("Ocho ");
        break;
      case 9:
        result.append("Nueve ");
        break;
    }

    return result;
  }

  public String getStringNumber(BigDecimal number, String moneda) {
    String cambio;
    if (moneda.equals("MXN")) {
      cambio = "Pesos ";
    } else if (moneda.equals("USD")) {
      cambio = "Dolares ";
    } else {
      cambio = "Pesos";
    }
    BigInteger decimal =
        number.remainder(BigDecimal.ONE).movePointRight(number.scale()).abs().toBigInteger();
    return cantidadConLetra(number.toString())
        .concat(cambio)
        .concat(cantidadConLetra(decimal.toString()))
        .concat(" Centavos");
  }
}
