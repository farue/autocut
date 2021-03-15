package de.farue.autocut.service.internetaccess;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Table;

class SwitchStatusParserTest {

    @Test
    void testParse() {
        String status = "\n"
            + "\n"
            + "Port      Name               Status       Vlan       Duplex  Speed Type \n"
            + "Gi1/0/1                      disabled     264          auto   auto 10/100/1000BaseTX\n"
            + "Gi1/0/2                      connected    264        a-full  a-100 10/100/1000BaseTX\n"
            + "Gi1/0/16                     connected    264        a-full a-1000 10/100/1000BaseTX\n"
            + "Gi1/0/17                     connected    264        a-full a-1000 10/100/1000BaseTX\n"
            + "Gi1/0/23                     notconnect   264          auto   auto 10/100/1000BaseTX\n"
            + "Gi1/0/24  ap-farue-keller2   connected    12         a-full a-1000 10/100/1000BaseTX\n"
            + "Gi1/1/1                      disabled     1            auto   auto Not Present\n"
            + "Te1/1/1   n3k-tuerme A       connected    trunk        full    10G SFP-10GBase-LR\n"
            + "Po1       Uplink n3k-tuerme  connected    trunk      a-full    10G \n"
            + "Po8                          notconnect   unassigned   auto   auto \n"
            + "Fa0                          disabled     routed       auto   auto 10/100BaseTX";

        SwitchStatusParser parser = new SwitchStatusParser();

        Table<String, String, String> statusTable = parser.parse(status);
        String expected = "{Gi1/0/1={Port=Gi1/0/1, Name=null, Status=disabled, Vlan=264, Duplex=auto, Speed=auto, Type=10/100/1000BaseTX}, "
            + "Gi1/0/2={Port=Gi1/0/2, Name=null, Status=connected, Vlan=264, Duplex=a-full, Speed=a-100, Type=10/100/1000BaseTX}, "
            + "Gi1/0/16={Port=Gi1/0/16, Name=null, Status=connected, Vlan=264, Duplex=a-full a, Speed=-1000, Type=10/100/1000BaseTX}, "
            + "Gi1/0/17={Port=Gi1/0/17, Name=null, Status=connected, Vlan=264, Duplex=a-full a, Speed=-1000, Type=10/100/1000BaseTX}, "
            + "Gi1/0/23={Port=Gi1/0/23, Name=null, Status=notconnect, Vlan=264, Duplex=auto, Speed=auto, Type=10/100/1000BaseTX}, "
            + "Gi1/0/24={Port=Gi1/0/24, Name=ap-farue-keller2, Status=connected, Vlan=12, Duplex=a-full a, Speed=-1000, Type=10/100/1000BaseTX}, "
            + "Gi1/1/1={Port=Gi1/1/1, Name=null, Status=disabled, Vlan=1, Duplex=auto, Speed=auto, Type=Not Present}, "
            + "Te1/1/1={Port=Te1/1/1, Name=n3k-tuerme A, Status=connected, Vlan=trunk, Duplex=full, Speed=10G, Type=SFP-10GBase-LR}, "
            + "Po1={Port=Po1, Name=Uplink n3k-tuerme, Status=connected, Vlan=trunk, Duplex=a-full, Speed=10G, Type=null}, "
            + "Po8={Port=Po8, Name=null, Status=notconnect, Vlan=unassigned, Duplex=auto, Speed=auto, Type=null}, "
            + "Fa0={Port=Fa0, Name=null, Status=disabled, Vlan=routed, Duplex=auto, Speed=auto, Type=10/100BaseTX}}";
        Assertions.assertThat(statusTable.toString()).isEqualTo(expected);
    }
}
