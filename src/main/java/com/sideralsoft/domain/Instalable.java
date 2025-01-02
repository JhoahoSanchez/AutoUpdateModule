package com.sideralsoft.domain;

import com.sideralsoft.utils.exception.InstalacionException;

public interface Instalable {

    void instalar() throws InstalacionException;
}
