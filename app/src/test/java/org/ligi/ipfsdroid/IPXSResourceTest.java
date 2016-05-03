package org.ligi.ipfsdroid;

import android.net.Uri;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.ligi.ipfsdroid.IPXSResource.IPXSType.IPFS;
import static org.ligi.ipfsdroid.IPXSResource.IPXSType.IPNS;


public class IPXSResourceTest {

    @Test
    public void testIPFSTypeIsDetected() {
        assertThat(new IPXSResource(Uri.parse("ipfs://foobar")).getType()).isEqualTo(IPFS);
    }

    @Test
    public void testIPNSTypeIsDetected() {
        assertThat(new IPXSResource(Uri.parse("ipns://foobar")).getType()).isEqualTo(IPNS);
    }


    @Test
    public void testIPNSAddressIsExtracted() {
        assertThat(new IPXSResource(Uri.parse("ipns://foo/bar")).getAddress()).isEqualTo("foo/bar");
    }


    @Test
    public void testIPFSAddressIsExtracted() {
        assertThat(new IPXSResource(Uri.parse("ipfs://foo/bar")).getAddress()).isEqualTo("foo/bar");
    }

    @Test
    public void testFSDetectsIPFSType() {
        assertThat(new IPXSResource(Uri.parse("fs://ipfs/bar")).getType()).isEqualTo(IPFS);
    }

    @Test
    public void testFSDetectsIPFSAddress() {
        assertThat(new IPXSResource(Uri.parse("fs://ipfs/foo/bar")).getAddress()).isEqualTo("foo/bar");
    }

    @Test
    public void testFSDetectsIPNSType() {
        assertThat(new IPXSResource(Uri.parse("fs://ipns/bar")).getType()).isEqualTo(IPNS);
    }

    @Test
    public void testFSDetectsIPNSAddress() {
        assertThat(new IPXSResource(Uri.parse("fs://ipns/foo/bar")).getAddress()).isEqualTo("foo/bar");
    }

    @Test
    public void testToStringWorks() {
        assertThat(new IPXSResource(Uri.parse("fs://ipns/foo/bar")).toString()).isEqualTo("ipns:foo/bar");
    }

    @Test
    public void testAuthorityLessFSUriWorks() {
        assertThat(new IPXSResource(Uri.parse("fs:/ipns/foo/bar")).toString()).isEqualTo("ipns:foo/bar");
    }

    @Test
    public void testIPFSIOUrlIsExtractedCorrectly() {
        assertThat(new IPXSResource(Uri.parse("http://ipfs.io/ipns/foo/bar")).toString()).isEqualTo("ipns:foo/bar");
    }
}