package com.kononikhin.footballbot;


import com.kononikhin.footballbot.bot.Utils;
import org.junit.Test;


public class FootballbotApplicationTests {


    @Test
    public void contextLoads() {

        var numberOfRows = Utils.defineNumberOfRows(1);
        assert numberOfRows == 1;
        var numberOfRows1 = Utils.defineNumberOfRows(4);
        assert numberOfRows1 == 1;
        var numberOfRows2 = Utils.defineNumberOfRows(5);
        assert numberOfRows2 == 2;
        var numberOfRows3 = Utils.defineNumberOfRows(8);
        assert numberOfRows3 == 2;
        var numberOfRows4 = Utils.defineNumberOfRows(13);
        assert numberOfRows4 == 4;
    }

}
