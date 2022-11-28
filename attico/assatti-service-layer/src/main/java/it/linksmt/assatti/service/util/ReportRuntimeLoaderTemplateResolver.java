/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package it.linksmt.assatti.service.util;

import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.resourceresolver.ClassLoaderResourceResolver;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import it.linksmt.assatti.datalayer.repository.ReportRuntimeRepository;


/**
 * <p>
 *   Implementation of {@link ITemplateResolver} that extends {@link TemplateResolver}
 *   and uses a {@link ClassLoaderResourceResolver} for resource resolution.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * @author Ivan Testaverde
 * 
 * @since 1.0
 *
 */
public class ReportRuntimeLoaderTemplateResolver
        extends TemplateResolver {

    
    
    
    public ReportRuntimeLoaderTemplateResolver(ReportRuntimeRepository reportRuntimeRepository ) {
        super();
        super.setResourceResolver(new ReportRuntimeLoaderResourceResolver(  reportRuntimeRepository ));
    }
    

    
    /**
     * <p>
     *   This method <b>should not be called</b>, because the resource resolver is
     *   fixed to be {@link ClassLoaderResourceResolver}. Every execution of this method
     *   will result in an exception.
     * </p>
     * <p>
     *   If you need to select a different resource resolver, use the {@link TemplateResolver}
     *   class instead.
     * </p>
     * 
     * @param resourceResolver the new resource resolver
     */
    @Override
    public void setResourceResolver(final IResourceResolver resourceResolver) {
        throw new ConfigurationException(
                "Cannot set a resource resolver on " + this.getClass().getName() + ". If " +
                "you want to set your own resource resolver, use " + TemplateResolver.class.getName() + 
                "instead");
    }

    
    
}
