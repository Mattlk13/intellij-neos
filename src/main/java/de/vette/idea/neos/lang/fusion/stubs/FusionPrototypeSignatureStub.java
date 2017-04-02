/*
 *  IntelliJ IDEA plugin to support the Neos CMS.
 *  Copyright (C) 2016  Christian Vette
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.vette.idea.neos.lang.fusion.stubs;

import com.intellij.psi.stubs.*;
import de.vette.idea.neos.lang.fusion.psi.*;
import de.vette.idea.neos.lang.fusion.psi.impl.FusionPrototypeSignatureImpl;
import de.vette.idea.neos.lang.fusion.stubs.index.FusionPrototypeDeclarationIndex;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class FusionPrototypeSignatureStub extends StubBase<FusionPrototypeSignature> {

    protected String namespace;
    protected String name;
    protected Boolean isDefinition;
    protected Boolean isInheritance;

    public FusionPrototypeSignatureStub(StubElement parent, IStubElementType elementType, String namespace, String name, Boolean isDefinition, Boolean isInheritance) {
        super(parent, elementType);

        this.namespace = namespace;
        this.name = name;
        this.isDefinition = isDefinition;
        this.isInheritance = isInheritance;
    }

    public static FusionStubElementType TYPE = new FusionStubElementType<FusionPrototypeSignatureStub , FusionPrototypeSignature>("FUSION_PROTOTYPE_SIGNATURE") {
        @Override
        public FusionPrototypeSignature createPsi(@NotNull FusionPrototypeSignatureStub stub) {
            return new FusionPrototypeSignatureImpl(stub, stub.getStubType());
        }

        @NotNull
        @Override
        public FusionPrototypeSignatureStub createStub(@NotNull FusionPrototypeSignature psi, StubElement parentStub) {
            String name = "";
            String namespace = "";
            Boolean isDefinition = false;
            Boolean isInheritance = false;

            if (psi.getParent() instanceof FusionPrototypeInheritance
                    && psi.getParent().getParent() instanceof FusionFile && psi.getPrevSibling() == null) {
                isDefinition = true;
                isInheritance = true;
            }

            if ((psi.getParent().getParent() instanceof FusionPropertyAssignment
                    || psi.getParent().getParent() instanceof FusionPropertyBlock)
                    && psi.getParent().getParent().getParent() instanceof FusionFile
                    && psi.getParent() instanceof FusionPath
                    && psi.getPrevSibling() == null) {
                isDefinition = true;
            }

            FusionType type = psi.getType();
            if (type != null) {
                if (type.getObjectTypeNamespace() != null) {
                    namespace = type.getObjectTypeNamespace().getText();
                }

                if (type.getUnqualifiedType() != null) {
                    name = type.getUnqualifiedType().getText();
                }
                return new FusionPrototypeSignatureStub(parentStub, psi.getElementType(), namespace, name, isDefinition, isInheritance);
            }
            return new FusionPrototypeSignatureStub(parentStub, psi.getElementType(), null, name, isDefinition, isInheritance);
        }

        @Override
        public void serialize(@NotNull FusionPrototypeSignatureStub stub, @NotNull StubOutputStream dataStream) throws IOException {
            dataStream.writeName(stub.namespace);
            dataStream.writeName(stub.name);
            dataStream.writeBoolean(stub.isDefinition);
            dataStream.writeBoolean(stub.isInheritance);
        }

        @NotNull
        @Override
        public FusionPrototypeSignatureStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
            return new FusionPrototypeSignatureStub(parentStub, this, getNameAsString(dataStream.readName()), getNameAsString(dataStream.readName()), dataStream.readBoolean(), dataStream.readBoolean());
        }

        @Override
        public void indexStub(@NotNull FusionPrototypeSignatureStub stub, @NotNull IndexSink sink) {
            if (stub.name != null && stub.isDefinition) {
                sink.occurrence(FusionPrototypeDeclarationIndex.KEY, stub.name);
            }
        }
    };
}