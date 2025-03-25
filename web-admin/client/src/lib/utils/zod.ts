import { ZodObject, ZodRawShape } from "zod";

export const zodUtil = {
  strictExtend<BaseShape extends ZodRawShape, ExtensionShape extends ZodRawShape>(
    base: ZodObject<BaseShape>,
    extension: ZodObject<ExtensionShape>
  ): ZodObject<BaseShape & ExtensionShape> {
    return base.merge(extension); 
  },
};