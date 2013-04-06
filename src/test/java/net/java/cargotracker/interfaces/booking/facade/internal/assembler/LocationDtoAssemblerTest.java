package net.java.cargotracker.interfaces.booking.facade.internal.assembler;

import net.java.cargotracker.interfaces.booking.facade.internal.assembler.LocationDtoAssembler;
import java.util.Arrays;
import java.util.List;
import net.java.cargotracker.domain.model.location.Location;
import net.java.cargotracker.domain.model.location.SampleLocations;

public class LocationDtoAssemblerTest {

    public void testToDTOList() {
        LocationDtoAssembler assembler = new LocationDtoAssembler();
        List<Location> locationList = Arrays.asList(
                SampleLocations.STOCKHOLM, SampleLocations.HAMBURG);

        List<net.java.cargotracker.interfaces.booking.facade.dto.Location> dtos =
                assembler.toDTOList(locationList);

        org.junit.Assert.assertEquals(2, dtos.size());

        net.java.cargotracker.interfaces.booking.facade.dto.Location dto = dtos.get(0);
        org.junit.Assert.assertEquals("SESTO", dto.getUnLocode());
        org.junit.Assert.assertEquals("SampleLocations.STOCKHOLM", dto.getName());

        dto = dtos.get(1);
        org.junit.Assert.assertEquals("DEHAM", dto.getUnLocode());
        org.junit.Assert.assertEquals("Hamburg", dto.getName());
    }
}