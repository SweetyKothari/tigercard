package com.sahaj.assignment.rule;


import com.sahaj.assignment.config.FareConfiguration;
import com.sahaj.assignment.dto.CommuteDetailDTO;
import com.sahaj.assignment.dto.ZoneDetail;
import com.sahaj.assignment.util.FareCalculatorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.sahaj.assignment.util.FareCalculatorUtil.printFareDetail;

/**
 * This class checks peak & non peak hrs & assign fare to each
 */
public class TimeCapedCalculator extends FareCalculator {

    static Logger logger = LoggerFactory.getLogger(TimeCapedCalculator.class);

    private FareCalculator fareCalculator;


    public double calculateFare(List<CommuteDetailDTO> commuteDetailDTOS, Map<LocalDate,ZoneDetail> dateZoneFarthestMap){
       double sum=0;
        for (CommuteDetailDTO commuteDetailDTO : commuteDetailDTOS)
        {
            boolean peakTime = FareCalculatorUtil.isPeakTime(commuteDetailDTO.getDate(), commuteDetailDTO.getTime());
            double fare = peakTime ? FareConfiguration.getZonePeakHrFareData().get(commuteDetailDTO.getFromZone()).get(commuteDetailDTO.getToZone())
                    : FareConfiguration.getZoneNonPeakHrFareData().get(commuteDetailDTO.getFromZone()).get(commuteDetailDTO.getToZone());
            sum+=fare;
            commuteDetailDTO.setFare(fare);
        }
        dateZoneFarthestMap = FareCalculatorUtil.computeFarthestZonePerDate(commuteDetailDTOS);
        logger.debug("Fare Computation After Running Time Caped Rules ");
        printFareDetail(Collections.unmodifiableList(commuteDetailDTOS));
        return fareCalculator!=null ?fareCalculator.calculateFare(commuteDetailDTOS,dateZoneFarthestMap):sum;
    }



    @Override
    public void setNextFareCalculator(FareCalculator fareCalculator) {
       this.fareCalculator=fareCalculator;

    }
}
