package br.com.monteiro;

import br.com.monteiro.exception.UnsupportedMathOperationException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MathController {

    @RequestMapping(value = "/sum/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    private Double sum (
            @PathVariable(value = "numberOne") String numberOne,
            @PathVariable(value = "numberTwo") String numberTwo
    ) throws Exception {

        if(!isNumeric(numberOne) || !isNumeric(numberTwo)){
            throw new UnsupportedMathOperationException("Please set a numeric value!");
        }
        return convertToDouble(numberOne) + convertToDouble(numberTwo);
    }

    @RequestMapping(value = "/sub/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    private Double sub (
            @PathVariable(value = "numberOne") String numberOne,
            @PathVariable(value = "numberTwo") String numberTwo
    ) throws Exception {

        if(!isNumeric(numberOne) || !isNumeric(numberTwo)){
            throw new UnsupportedMathOperationException("Please set a numeric value!");
        }
        return convertToDouble(numberOne) - convertToDouble(numberTwo);
    }

    @RequestMapping(value = "/mul/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    private Double mul (
            @PathVariable(value = "numberOne") String numberOne,
            @PathVariable(value = "numberTwo") String numberTwo
    ) throws Exception {

        if(!isNumeric(numberOne) || !isNumeric(numberTwo)){
            throw new UnsupportedMathOperationException("Please set a numeric value!");
        }
        return convertToDouble(numberOne) * convertToDouble(numberTwo);
    }

    @RequestMapping(value = "/div/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    private Double div (
            @PathVariable(value = "numberOne") String numberOne,
            @PathVariable(value = "numberTwo") String numberTwo
    ) throws Exception {

        if(!isNumeric(numberOne) || !isNumeric(numberTwo)){
            throw new UnsupportedMathOperationException("Please set a numeric value!");
        }
        return convertToDouble(numberOne) / convertToDouble(numberTwo);
    }

    @RequestMapping(value = "/med/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    private Double med (
            @PathVariable(value = "numberOne") String numberOne,
            @PathVariable(value = "numberTwo") String numberTwo
    ) throws Exception {

        if(!isNumeric(numberOne) || !isNumeric(numberTwo)){
            throw new UnsupportedMathOperationException("Please set a numeric value!");
        }
        return (convertToDouble(numberOne) + convertToDouble(numberTwo)) / 2;
    }

    @RequestMapping(value = "/qua/{numberOne}", method = RequestMethod.GET)
    private Double qua (
            @PathVariable(value = "numberOne") String numberOne
    ) throws Exception {

        if(!isNumeric(numberOne)){
            throw new UnsupportedMathOperationException("Please set a numeric value!");
        }
        return Math.sqrt(convertToDouble(numberOne));
    }

    private Double convertToDouble(String strNumber) {
        if(strNumber == null) return 0D;
        String number = strNumber.replaceAll(",",".");
        if(isNumeric(number))
            return Double.parseDouble(number);
        return 0D;
    }

    private boolean isNumeric(String strNumber){
        if(strNumber == null) return false;
        String number = strNumber.replaceAll(",",".");
        return number.matches("[-+]?[0-9]*\\.?[0-9]+");
    }

}

