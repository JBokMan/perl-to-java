package com.sippy.wrapper.parent;

import com.sippy.wrapper.parent.database.DatabaseConnection;
import com.sippy.wrapper.parent.database.dao.TnbDao;
import com.sippy.wrapper.parent.dto.TnbDto;
import com.sippy.wrapper.parent.request.GetTnbRequest;
import com.sippy.wrapper.parent.request.JavaTestRequest;
import com.sippy.wrapper.parent.response.JavaTestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Stateless
public class WrappedMethods {

  private static final Logger LOGGER = LoggerFactory.getLogger(WrappedMethods.class);

  @EJB DatabaseConnection databaseConnection;

  @RpcMethod(name = "javaTest", description = "Check if everything works :)")
  public Map<String, Object> javaTest(JavaTestRequest request) {
    JavaTestResponse response = new JavaTestResponse();

    int count = databaseConnection.getAllTnbs().size();

    LOGGER.info("the count is: " + count);

    response.setId(request.getId());
    String tempFeeling = request.isTemperatureOver20Degree() ? "warm" : "cold";
    response.setOutput(
        String.format(
            "%s has a rather %s day. And he has %d tnbs", request.getName(), tempFeeling, count));

    Map<String, Object> jsonResponse = new HashMap<>();
    jsonResponse.put("faultCode", "200");
    jsonResponse.put("faultString", "Method success");
    jsonResponse.put("something", response);

    return jsonResponse;
  }

  @RpcMethod(name = "getTnbList")
  public Map<String, Object> getTnbList(GetTnbRequest request) {
    final var number = request.number();
    LOGGER.info("Start getTnbList with number: {}", number);
    LOGGER.info("Fetching TNB list from the database...");

    final var allTnbs = databaseConnection.getAllTnbs();

    Optional<TnbDao> tnbMatchingNumber = Optional.empty();
    if (number != null) {
      tnbMatchingNumber = allTnbs
              .stream()
              .filter(tnbDao -> Objects.equals(tnbDao.getTnb(), number))
              .findFirst();
    }
    var tnbMatchingNumberIsTnb = false;
    if (tnbMatchingNumber.isPresent()) {
      tnbMatchingNumberIsTnb = tnbMatchingNumber.get().getTnb().equals("D001");
    }

    final ArrayList<TnbDto> tnbs = new ArrayList();
    tnbs.add(new TnbDto("D001", "Deutsche Telekom", tnbMatchingNumberIsTnb));

    for (TnbDao tnb : allTnbs) {
      if (tnb.getTnb().equals("D146") || tnb.getTnb().equals("D218") || tnb.getTnb().equals("D248")) {
        continue;
      }
      var matchesNumberFromRequest = false;
      if (tnbMatchingNumber.isPresent()) {
        matchesNumberFromRequest = tnbMatchingNumber.get().getTnb().equals(tnb.getTnb());
      }
      tnbs.add(new TnbDto(tnb.getTnb(), tnb.getName(), matchesNumberFromRequest));
    }

    tnbs.sort(Comparator.comparing(tnbDto -> tnbDto.name().toLowerCase()));

    Map<String, Object> jsonResponse = new HashMap<>();
    jsonResponse.put("faultCode", "200");
    jsonResponse.put("faultString", "Method success");
    jsonResponse.put("tnbs", tnbs);

    return jsonResponse;
  }
}
