package org.apereo.cas.web;

import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.configuration.model.support.captcha.GoogleRecaptchaProperties;
import org.apereo.cas.services.DefaultRegisteredServiceProperty;
import org.apereo.cas.services.RegisteredServiceProperty;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.util.MockServletContext;
import org.apereo.cas.web.support.WebUtils;

import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.webflow.context.ExternalContextHolder;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockRequestContext;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This is {@link DefaultCaptchaActivationStrategyTests}.
 *
 * @author Misagh Moayyed
 * @since 6.5.0
 */
@Tag("Simple")
public class DefaultCaptchaActivationStrategyTests {

    private static MockRequestContext getRequestContext() {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();
        context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, response));
        RequestContextHolder.setRequestContext(context);
        ExternalContextHolder.setExternalContext(context.getExternalContext());
        return context;
    }

    @Test
    public void verifyByProps() {
        val strategy = new DefaultCaptchaActivationStrategy(mock(ServicesManager.class));
        val context = getRequestContext();

        val properties = new GoogleRecaptchaProperties().setEnabled(true);
        assertTrue(strategy.shouldActivate(context, properties).isPresent());

        properties.setEnabled(false);
        assertTrue(strategy.shouldActivate(context, properties).isEmpty());
    }

    @Test
    public void verifyByService() {
        val servicesManager = mock(ServicesManager.class);

        val strategy = new DefaultCaptchaActivationStrategy(servicesManager);
        val context = getRequestContext();

        val service = RegisteredServiceTestUtils.getService(UUID.randomUUID().toString());
        val registeredService = RegisteredServiceTestUtils.getRegisteredService(service.getId());
        registeredService.getProperties().put(RegisteredServiceProperty.RegisteredServiceProperties.CAPTCHA_ENABLED.getPropertyName(),
            new DefaultRegisteredServiceProperty("true"));
        when(servicesManager.findServiceBy(any(Service.class))).thenReturn(registeredService);
        
        WebUtils.putServiceIntoFlowScope(context, service);
        val properties = new GoogleRecaptchaProperties().setEnabled(false);
        assertTrue(strategy.shouldActivate(context, properties).isPresent());
    }
}