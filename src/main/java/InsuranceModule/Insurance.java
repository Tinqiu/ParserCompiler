package InsuranceModule;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Insurance {

    private Integer policyID;
    private String stateCode;
    private String county;
    private Double eqSiteLimit;
    private Double huSiteLimit;
    private Double flSiteLimit;
    private Double frSiteLimit;
    private Double tiv2011;
    private Double tiv2012;
    private Double eqSiteDeductible;
    private Double huSiteDeductible;
    private Double flSiteDeductible;
    private Double frSiteDeductible;
    private Double pointLatitude;
    private Double pointLongitude;
    private Line line;
    private Construction construction;
    private Integer pointGranularity;


}
