package InsuranceModule;

import lombok.extern.java.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log
public class InsuranceDAO {
    public static List<Insurance> streamCsvDataToInsurance(String path){
        List<Insurance> insurances = new ArrayList<>();

        //Stream in the insurance data into lines split by comma, build a list containing lines and then collect each line into a list
        try(Stream<String> lines = Files.lines(Paths.get(path))){
            List<List<String>> places = lines.skip(1).map(line -> Arrays.asList(line.split(","))).collect(Collectors.toList());

            //A bit of a ghetto use of a builder, but I wanted to try the Lombok @Builder annotation
//            places.forEach(place -> insurances.add(new Insurance.InsuranceBuilder()
//                    .policyID(Integer.parseInt(place.get(0)))
//                    .stateCode(place.get(1))
//                    .county(place.get(2))
//                    .eqSiteLimit(Double.parseDouble(place.get(3)))
//                    .huSiteLimit(Double.parseDouble(place.get(4)))
//                    .flSiteLimit(Double.parseDouble(place.get(5)))
//                    .frSiteLimit(Double.parseDouble(place.get(6)))
//                    .tiv2011(Double.parseDouble(place.get(7)))
//                    .tiv2012(Double.parseDouble(place.get(8)))
//                    .eqSiteDeductible(Double.parseDouble(place.get(9)))
//                    .huSiteDeductible(Double.parseDouble(place.get(10)))
//                    .flSiteDeductible(Double.parseDouble(place.get(11)))
//                    .frSiteDeductible(Double.parseDouble(place.get(12)))
//                    .pointLatitude(Double.parseDouble(place.get(13)))
//                    .pointLongitude(Double.parseDouble(place.get(14)))
//                    .line(Line.valueOf(place.get(15).toUpperCase()))
//                    .construction(Construction.valueOf(place.get(16).toUpperCase().replace(" ", "_")))
//                    .pointGranularity(Integer.parseInt(place.get(17)))
//                    .build()));

        }catch (IOException ex){
            ex.printStackTrace();
        }
        return insurances;
    }
    public static List<Insurance> readCsvDataToInsurance(String path){
        List<Insurance> insurances = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(path))){
            //get rid of the headers
            reader.readLine();
            String line;

            while((line = reader.readLine()) != null){
                List<String> insuranceLine = Arrays.asList(line.split(","));
//                insurances.add(new Insurance.InsuranceBuilder()
//                        .policyID(Integer.parseInt(insuranceLine.get(0)))
//                        .stateCode(insuranceLine.get(1))
//                        .county(insuranceLine.get(2))
//                        .eqSiteLimit(Double.parseDouble(insuranceLine.get(3)))
//                        .huSiteLimit(Double.parseDouble(insuranceLine.get(4)))
//                        .flSiteLimit(Double.parseDouble(insuranceLine.get(5)))
//                        .frSiteLimit(Double.parseDouble(insuranceLine.get(6)))
//                        .tiv2011(Double.parseDouble(insuranceLine.get(7)))
//                        .tiv2012(Double.parseDouble(insuranceLine.get(8)))
//                        .eqSiteDeductible(Double.parseDouble(insuranceLine.get(9)))
//                        .huSiteDeductible(Double.parseDouble(insuranceLine.get(10)))
//                        .flSiteDeductible(Double.parseDouble(insuranceLine.get(11)))
//                        .frSiteDeductible(Double.parseDouble(insuranceLine.get(12)))
//                        .pointLatitude(Double.parseDouble(insuranceLine.get(13)))
//                        .pointLongitude(Double.parseDouble(insuranceLine.get(14)))
//                        .line(Line.valueOf(insuranceLine.get(15).toUpperCase()))
//                        .construction(Construction.valueOf(insuranceLine.get(16).toUpperCase().replace(" ", "_")))
//                        .pointGranularity(Integer.parseInt(insuranceLine.get(17)))
//                        .build());
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return insurances;
    }
}
